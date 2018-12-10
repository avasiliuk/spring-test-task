package com.avasiliuk.testtask.utils.mappers;


import com.avasiliuk.testtask.model.jooq.tables.records.OrdersRecord;
import com.avasiliuk.testtask.model.rest.NewOrder;
import com.avasiliuk.testtask.model.rest.Order;
import com.avasiliuk.testtask.model.rest.Product;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = GeneralMapper.class)
public abstract class OrderMapper {

    public abstract OrdersRecord toOrdersRecord(NewOrder newOrder, UUID id, Timestamp created);

    public abstract Order toOrder(OrdersRecord ordersRecord, BigDecimal totalAmount, List<Product> products);
}
