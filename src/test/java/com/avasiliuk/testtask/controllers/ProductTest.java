package com.avasiliuk.testtask.controllers;

import com.avasiliuk.testtask.model.jooq.tables.records.ProductsRecord;
import com.avasiliuk.testtask.test.AbstractTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductTest extends AbstractTest {

    @Test
    public void create() throws Exception {
        //WHEN
        final ResultActions resultActions = mvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"prod1\", \"price\": 1.41234}"));
        //THEN
        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("prod1")))
                .andExpect(jsonPath("$.price", is(1.41)));
    }

    @Test
    public void createWithEmptyFields() throws Exception {
        //WHEN
        final ResultActions resultActions = mvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));
        //THEN
        resultActions.andExpect(status().isUnprocessableEntity())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    public void createDuplicateName() throws Exception {
        //GIVEN
        dslContext.executeInsert(new ProductsRecord(UUID.randomUUID(), "prod1", new BigDecimal(1.40)));
        //WHEN
        final ResultActions resultActions = mvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"prod1\", \"price\": 1.40}"));
        //THEN
        resultActions
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void update() throws Exception {
        //GIVEN
        final UUID id = UUID.randomUUID();
        dslContext.executeInsert(new ProductsRecord(id, "prod1", new BigDecimal(1.40)));
        //WHEN
        final ResultActions resultActions = mvc.perform(put("/product/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"prod2\", \"price\": 2.3012}"));
        //THEN
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("prod2")))
                .andExpect(jsonPath("$.price", is(2.30)));
    }

    @Test
    public void updateNotExisiting() throws Exception {
        //WHEN
        final ResultActions resultActions = mvc.perform(put("/product/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"prod2\", \"price\": 2.30}"));
        //THEN
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())));
    }

    @Test
    public void updateDuplicateName() throws Exception {
        //GIVEN
        final UUID id = UUID.randomUUID();
        dslContext.executeInsert(new ProductsRecord(id, "prod1", new BigDecimal(1.40)));
        dslContext.executeInsert(new ProductsRecord(UUID.randomUUID(), "prod2", new BigDecimal(1.41)));
        //WHEN
        final ResultActions resultActions = mvc.perform(put("/product/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"prod2\", \"price\": 2.30}"));
        //THEN
        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void delete() throws Exception {
        //GIVEN
        final UUID id = UUID.randomUUID();
        dslContext.executeInsert(new ProductsRecord(id, "prod1", new BigDecimal(1.40)));
        //WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.delete("/product/{id}", id));
        //THEN
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    public void list() throws Exception {
        //GIVEN
        final UUID id1 = UUID.randomUUID();
        dslContext.executeInsert(new ProductsRecord(id1, "prod1", new BigDecimal(1.40)));
        final UUID id2 = UUID.randomUUID();
        dslContext.executeInsert(new ProductsRecord(id2, "prod2", new BigDecimal(1.41)));
        final UUID id3 = UUID.randomUUID();
        dslContext.executeInsert(new ProductsRecord(id3, "prod3", new BigDecimal(1.42)));
        //WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/products"));
        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[?(@.id == '" + id1 + "')]").exists())
                .andExpect(jsonPath("$[?(@.id == '" + id2 + "')]").exists())
                .andExpect(jsonPath("$[?(@.id == '" + id3 + "')]").exists());
    }

    @Test
    public void get() throws Exception {
        //GIVEN
        final UUID id = UUID.randomUUID();
        dslContext.executeInsert(new ProductsRecord(id, "prod1", new BigDecimal(1.40)));
        //WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/product/{id}", id));
        //THEN
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("prod1")))
                .andExpect(jsonPath("$.price", is(1.40)));
    }

    @Test
    public void getNotExisting() throws Exception {
        //WHEN
        final ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/product/{id}", UUID.randomUUID()));
        //THEN
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())));
    }

}