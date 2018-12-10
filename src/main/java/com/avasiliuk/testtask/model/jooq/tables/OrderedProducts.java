/*
 * This file is generated by jOOQ.
 */
package com.avasiliuk.testtask.model.jooq.tables;


import com.avasiliuk.testtask.model.jooq.Indexes;
import com.avasiliuk.testtask.model.jooq.Keys;
import com.avasiliuk.testtask.model.jooq.Public;
import com.avasiliuk.testtask.model.jooq.tables.records.OrderedProductsRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@Generated(
        value = {
                "http://www.jooq.org",
                "jOOQ version:3.11.7"
        },
        comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class OrderedProducts extends TableImpl<OrderedProductsRecord> {

    /**
     * The reference instance of <code>PUBLIC.ORDERED_PRODUCTS</code>
     */
    public static final OrderedProducts ORDERED_PRODUCTS = new OrderedProducts();
    private static final long serialVersionUID = 126022148;
    /**
     * The column <code>PUBLIC.ORDERED_PRODUCTS.ORDER_ID</code>.
     */
    public final TableField<OrderedProductsRecord, UUID> ORDER_ID = createField("ORDER_ID", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");
    /**
     * The column <code>PUBLIC.ORDERED_PRODUCTS.PRODUCT_ID</code>.
     */
    public final TableField<OrderedProductsRecord, UUID> PRODUCT_ID = createField("PRODUCT_ID", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");
    /**
     * The column <code>PUBLIC.ORDERED_PRODUCTS.ORDERED_PRICE</code>.
     */
    public final TableField<OrderedProductsRecord, BigDecimal> ORDERED_PRICE = createField("ORDERED_PRICE", org.jooq.impl.SQLDataType.DECIMAL(20, 2).nullable(false), this, "");

    /**
     * Create a <code>PUBLIC.ORDERED_PRODUCTS</code> table reference
     */
    public OrderedProducts() {
        this(DSL.name("ORDERED_PRODUCTS"), null);
    }

    /**
     * Create an aliased <code>PUBLIC.ORDERED_PRODUCTS</code> table reference
     */
    public OrderedProducts(String alias) {
        this(DSL.name(alias), ORDERED_PRODUCTS);
    }

    /**
     * Create an aliased <code>PUBLIC.ORDERED_PRODUCTS</code> table reference
     */
    public OrderedProducts(Name alias) {
        this(alias, ORDERED_PRODUCTS);
    }

    private OrderedProducts(Name alias, Table<OrderedProductsRecord> aliased) {
        this(alias, aliased, null);
    }

    private OrderedProducts(Name alias, Table<OrderedProductsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> OrderedProducts(Table<O> child, ForeignKey<O, OrderedProductsRecord> key) {
        super(child, key, ORDERED_PRODUCTS);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrderedProductsRecord> getRecordType() {
        return OrderedProductsRecord.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.FK_ORDERS_ID_INDEX_2, Indexes.FK_PRODUCTS_ID_INDEX_2, Indexes.PRIMARY_KEY_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<OrderedProductsRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<OrderedProductsRecord>> getKeys() {
        return Arrays.<UniqueKey<OrderedProductsRecord>>asList(Keys.CONSTRAINT_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<OrderedProductsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<OrderedProductsRecord, ?>>asList(Keys.FK_ORDERS_ID, Keys.FK_PRODUCTS_ID);
    }

    public Orders orders() {
        return new Orders(this, Keys.FK_ORDERS_ID);
    }

    public Products products() {
        return new Products(this, Keys.FK_PRODUCTS_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderedProducts as(String alias) {
        return new OrderedProducts(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderedProducts as(Name alias) {
        return new OrderedProducts(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public OrderedProducts rename(String name) {
        return new OrderedProducts(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public OrderedProducts rename(Name name) {
        return new OrderedProducts(name, null);
    }
}