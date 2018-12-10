package com.avasiliuk.testtask.test;

import com.avasiliuk.testtask.model.jooq.Tables;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.Batch;
import org.jooq.DSLContext;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractTest {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected DSLContext dslContext;

    @After
    public void clearDatabase() {
        //no truncate here since delete is much faster on small tables
        final Batch batch = dslContext.batch(
                dslContext.delete(Tables.ORDERED_PRODUCTS),
                dslContext.delete(Tables.ORDERS),
                dslContext.delete(Tables.PRODUCTS)
        );
        batch.execute();
    }
}