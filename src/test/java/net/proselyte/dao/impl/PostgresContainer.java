package net.proselyte.dao.impl;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {
    private static PostgresContainer container;

    private PostgresContainer() {
        super("postgres:15-alpine");
    }

    public static PostgresContainer getTestContainerInstance() {
        if (container == null) {
            container = new PostgresContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
    }
}
