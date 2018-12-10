package com.avasiliuk.testtask.services;

import com.avasiliuk.testtask.model.jooq.tables.records.OrderedProductsRecord;
import com.avasiliuk.testtask.model.jooq.tables.records.OrdersRecord;
import com.avasiliuk.testtask.model.jooq.tables.records.ProductsRecord;
import com.avasiliuk.testtask.model.rest.NewOrder;
import com.avasiliuk.testtask.model.rest.Order;
import com.avasiliuk.testtask.model.rest.Product;
import com.avasiliuk.testtask.utils.mappers.OrderMapper;
import com.avasiliuk.testtask.utils.mappers.ProductMapper;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.avasiliuk.testtask.model.jooq.Tables.ORDERED_PRODUCTS;
import static com.avasiliuk.testtask.model.jooq.Tables.ORDERS;
import static com.avasiliuk.testtask.model.jooq.Tables.PRODUCTS;

@Transactional
@Service
public class OrderService {

    private OrderMapper orderMapper;
    private ProductMapper productMapper;
    private DSLContext dslContext;

    @Autowired
    public OrderService(final OrderMapper orderMapper,
                        final ProductMapper productMapper,
                        final DSLContext dslContext) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.dslContext = dslContext;
    }


    public Order create(NewOrder newOrder) {
        if (CollectionUtils.isEmpty(newOrder.getProductIds())) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "'productIds' must not be empty");
        }
        final UUID id = UUID.randomUUID();
        final OrdersRecord ordersRecord = orderMapper.toOrdersRecord(newOrder, id, Timestamp.from(Instant.now()));
        dslContext.executeInsert(ordersRecord);

        final Result<ProductsRecord> products = dslContext.selectFrom(PRODUCTS)
                .where(PRODUCTS.ID.in(newOrder.getProductIds())).fetch();

        final List<OrderedProductsRecord> orderedProducts = products.stream().map(productsRecord -> {
            final OrderedProductsRecord record = new OrderedProductsRecord();
            record.setOrderedPrice(productsRecord.getPrice());
            record.setProductId(productsRecord.getId());
            record.setOrderId(id);
            return record;
        }).collect(Collectors.toList());

        final BigDecimal total = products.stream().map(ProductsRecord::getPrice).reduce(BigDecimal::add).get();
        dslContext.batchInsert(orderedProducts).execute();
        return orderMapper.toOrder(ordersRecord, total, productMapper.toProductList(products));
    }

    public void delete(UUID id) {
        dslContext.deleteFrom(ORDERS).where(ORDERS.ID.eq(id));
    }

    public List<Order> list(Instant from, Instant to) {
        Condition condition = DSL.trueCondition();
        if (from != null) {
            condition = condition.and(ORDERS.CREATED.ge(Timestamp.from(from)));
        }
        if (from != null) {
            condition = condition.and(ORDERS.CREATED.le(Timestamp.from(to)));
        }
        final Result<OrdersRecord> ordersRecords = dslContext.selectFrom(ORDERS).where(condition)
                .orderBy(ORDERS.CREATED)
                .fetch();

        final List<UUID> orderIds = ordersRecords.stream().map(OrdersRecord::getId).collect(Collectors.toList());

        final Result<Record> joinedOrderedProducs = dslContext.select().from(ORDERED_PRODUCTS)
                .join(PRODUCTS).on(ORDERED_PRODUCTS.PRODUCT_ID.eq(PRODUCTS.ID))
                .where(ORDERED_PRODUCTS.ORDER_ID.in(orderIds))
                .fetch();

        final Map<UUID, List<Record>> recordsByOrderId = joinedOrderedProducs.stream()
                .collect(Collectors.groupingBy(rec -> rec.get(ORDERED_PRODUCTS.ORDER_ID)));

        return ordersRecords.stream().map(ordersRecord -> {
            final List<Record> joined = recordsByOrderId.get(ordersRecord.getId());
            return mapOrder(ordersRecord, joined);
        }).collect(Collectors.toList());
    }

    public Order get(UUID id) {
        final OrdersRecord ordersRecord = dslContext.selectFrom(ORDERS).where(ORDERS.ID.eq(id)).fetchOne();

        final Result<Record> joinedOrderedProducts = dslContext.select().from(ORDERED_PRODUCTS)
                .join(PRODUCTS).on(ORDERED_PRODUCTS.PRODUCT_ID.eq(PRODUCTS.ID))
                .where(ORDERED_PRODUCTS.ORDER_ID.eq(id))
                .fetch();
        return mapOrder(ordersRecord, joinedOrderedProducts);
    }

    private List<Product> mapJoinedOrderedProducts(List<Record> joinedOrderedProducts) {
        return joinedOrderedProducts.stream().map(productRecord -> {
            final OrderedProductsRecord ordered = productRecord.into(ORDERED_PRODUCTS);
            final ProductsRecord prod = productRecord.into(PRODUCTS);
            final Product product = productMapper.toProduct(prod);
            product.setPrice(ordered.getOrderedPrice());
            return product;
        }).collect(Collectors.toList());
    }

    private Order mapOrder(OrdersRecord ordersRecord, List<Record> joinedOrderedProducts) {
        final BigDecimal total = joinedOrderedProducts.stream().map(p -> p.get(ORDERED_PRODUCTS.ORDERED_PRICE))
                .reduce(BigDecimal::add).get();

        final List<Product> list = mapJoinedOrderedProducts(joinedOrderedProducts);
        return orderMapper.toOrder(ordersRecord, total, list);

    }
}
