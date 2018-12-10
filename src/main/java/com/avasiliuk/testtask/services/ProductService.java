package com.avasiliuk.testtask.services;

import com.avasiliuk.testtask.model.jooq.tables.records.ProductsRecord;
import com.avasiliuk.testtask.model.rest.NewProduct;
import com.avasiliuk.testtask.model.rest.Product;
import com.avasiliuk.testtask.utils.Util;
import com.avasiliuk.testtask.utils.mappers.ProductMapper;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.avasiliuk.testtask.model.jooq.Tables.PRODUCTS;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Transactional
@Service
public class ProductService {

    private ProductMapper productMapper;
    private DSLContext dslContext;

    @Autowired
    public ProductService(final ProductMapper productMapper,
                          final DSLContext dslContext) {
        this.productMapper = productMapper;
        this.dslContext = dslContext;
    }

    public Product update(UUID id, NewProduct newProduct) {
        final ProductsRecord record = productMapper.toProductsRecord(newProduct);
        try {
            final int res = dslContext.update(PRODUCTS).set(record).where(PRODUCTS.ID.eq(id)).execute();
            if (res == 0) {
                throw new ApiException(NOT_FOUND, "Product with id '%s' not found ", id);
            }
        } catch (DataAccessException e) {
            if (Util.checkConstraintViolationOrThrow(e, "unique_products_name")) {
                throw new ApiException(UNPROCESSABLE_ENTITY,
                        "Product with name '%s' already exist", newProduct.getName());
            }
        }
        //reload from db to update price precision
        final ProductsRecord saved = dslContext.selectFrom(PRODUCTS).where(PRODUCTS.ID.eq(id)).fetchOne();
        return productMapper.toProduct(saved);
    }

    public Product create(NewProduct newProduct) {
        final ProductsRecord record = productMapper.toProductsRecord(newProduct);
        final UUID id = UUID.randomUUID();
        record.setId(id);
        try {
            dslContext.executeInsert(record);
        } catch (DataAccessException e) {
            if (Util.checkConstraintViolationOrThrow(e, "unique_products_name")) {
                throw new ApiException(UNPROCESSABLE_ENTITY, "Product with name '%s' already exist", newProduct.getName());
            }
        }
        //reload from db to update price precision
        final ProductsRecord saved = dslContext.selectFrom(PRODUCTS).where(PRODUCTS.ID.eq(id)).fetchOne();
        return productMapper.toProduct(saved);
    }

    public void delete(UUID id) {
        dslContext.deleteFrom(PRODUCTS).where(PRODUCTS.ID.eq(id));
    }

    public Product get(UUID id) {
        final ProductsRecord productsRecord = dslContext.selectFrom(PRODUCTS)
                .where(PRODUCTS.ID.eq(id)).fetchOne();
        if (productsRecord == null) {
            throw new ApiException(NOT_FOUND, "Product with id '%s' not found", id);
        }
        return productMapper.toProduct(productsRecord);
    }

    public List<Product> list() {
        final Result<ProductsRecord> products = dslContext.selectFrom(PRODUCTS).fetch();
        return productMapper.toProductList(products);
    }
}
