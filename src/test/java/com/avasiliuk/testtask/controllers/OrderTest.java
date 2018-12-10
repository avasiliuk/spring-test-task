package com.avasiliuk.testtask.controllers;

import com.avasiliuk.testtask.model.jooq.tables.records.ProductsRecord;
import com.avasiliuk.testtask.model.rest.NewOrder;
import com.avasiliuk.testtask.test.AbstractTest;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static com.avasiliuk.testtask.model.jooq.Tables.ORDERS;
import static com.avasiliuk.testtask.model.jooq.Tables.PRODUCTS;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderTest extends AbstractTest {
    @Test
    public void create() throws Exception {
        //GIVEN
        final UUID[] ids = Arrays.copyOf(createProducts(3), 4);
        ids[3] = UUID.randomUUID();//not existing product id
        //WHEN
        final ResultActions resultActions = mvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NewOrder(asList(ids), "buyer@mail.test"))));
        //THEN
        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.buyerEmail", is("buyer@mail.test")))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.totalAmount", is(7.2)))
                .andExpect(jsonPath("$.products", hasSize(3)))
                .andExpect(jsonPath("$.products[?(@.id == '" + ids[0] + "' && @.name=='prod1' && @.price==1.40)]").exists())
                .andExpect(jsonPath("$.products[?(@.id == '" + ids[1] + "' && @.name=='prod2' && @.price==2.40)]").exists())
                .andExpect(jsonPath("$.products[?(@.id == '" + ids[2] + "' && @.name=='prod3' && @.price==3.40)]").exists());
    }

    @Test
    public void createWithNoProducts() throws Exception {
        //WHEN
        final ResultActions resultActions = mvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NewOrder(new ArrayList<>(), "buyer@mail.test"))));
        //THEN
        resultActions.andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    public void get() throws Exception {
        //GIVEN
        final UUID[] ids = createProducts(3);
        final UUID orderId = createOrder(ids);
        //check order total price not changes
        dslContext.update(PRODUCTS).set(PRODUCTS.PRICE, new BigDecimal(0.1)).where(PRODUCTS.ID.eq(ids[0]));
        //WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/order/{id}", orderId));
        //THEN
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.buyerEmail", is("buyer@mail.test")))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.totalAmount", is(7.2)))
                .andExpect(jsonPath("$.products", hasSize(3)))
                .andExpect(jsonPath("$.products[?(@.id == '" + ids[0] + "' && @.name=='prod1' && @.price==1.40)]").exists())
                .andExpect(jsonPath("$.products[?(@.id == '" + ids[1] + "' && @.name=='prod2' && @.price==2.40)]").exists())
                .andExpect(jsonPath("$.products[?(@.id == '" + ids[2] + "' && @.name=='prod3' && @.price==3.40)]").exists());
    }

    @Test
    public void delete() throws Exception {
        //GIVEN
        final UUID orderId = createOrder(createProducts(3));
        //WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.delete("/order/{id}", orderId));
        //THEN
        resultActions.andExpect(status().isNoContent());
        Assert.assertFalse(dslContext.fetchExists(PRODUCTS, PRODUCTS.ID.eq(orderId)));
    }

    @Test
    public void list() throws Exception {
        //GIVEN
        Instant from = Instant.now();
        final UUID[] ids = createProducts(11);
        final UUID orderId1 = createOrder(ids[0], ids[1], ids[2]);
        createOrder(ids[3], ids[4]);
        createOrder(ids[5], ids[6]);
        Thread.sleep(200);
        Instant to = Instant.now();
        Thread.sleep(200);
        createOrder(ids[7], ids[8]);
        createOrder(ids[9], ids[10]);
        //WHEN
        final ResultActions resultActions1 = mvc.perform(MockMvcRequestBuilders.get("/orders")
                .param("from", from.toString()).param("to", to.toString()));
        //THEN
        resultActions1.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(orderId1.toString())))
                .andExpect(jsonPath("$[0].buyerEmail", is("buyer@mail.test")))
                .andExpect(jsonPath("$[0].created", notNullValue()))
                .andExpect(jsonPath("$[0].totalAmount", is(7.20)))
                .andExpect(jsonPath("$[0].products", hasSize(3)))
                .andExpect(jsonPath("$[0].products[?(@.id == '" + ids[0] + "' && @.name=='prod1' && @.price==1.40)]").exists());
        //WHEN
        final ResultActions resultActions2 = mvc.perform(MockMvcRequestBuilders.get("/orders"));
        //THEN
        resultActions2.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5)));
    }

    private UUID[] createProducts(int count) {
        final UUID[] ids = new UUID[count];
        for (int i = 0; i < count; i++) {
            ids[i] = UUID.randomUUID();
            dslContext.executeInsert(new ProductsRecord(ids[i], "prod" + (i + 1), new BigDecimal((i + 1) + 0.40)));

        }
        return ids;
    }

    @SneakyThrows
    private UUID createOrder(UUID... ids) {
        mvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NewOrder(asList(ids), "buyer@mail.test"))))
                .andExpect(status().isCreated());
        return dslContext.selectFrom(ORDERS).fetchAny(ORDERS.ID);
    }
}