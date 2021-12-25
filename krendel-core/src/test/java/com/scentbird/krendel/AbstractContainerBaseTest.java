package com.scentbird.krendel;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerBaseTest {

    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER;

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer("postgres:10-alpine")
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("password");
        POSTGRES_CONTAINER.start();
    }

    public AbstractContainerBaseTest() {

    }

}
