package com.tritonkor.persistence.util;


import java.lang.reflect.Proxy;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Manages a pool of database connections.
 * This class initializes a pool of connections and provides methods to obtain and close connections.
 */
@Component
public class ConnectionManager {

    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int DEFAULT_POOL_SIZE= 10;
    private final PropertyManager propertyManager;
    private static BlockingQueue<Connection> pool;
    private static List<Connection> sourceConnections;
    final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    /**
     * Constructs a ConnectionManager and initializes the connection pool.
     *
     * @param propertyManager the PropertyManager to fetch database properties
     */
    public ConnectionManager(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
        initConnectionPool();
    }

    /**
     * Retrieves a connection from the pool.
     *
     * @return a Connection object from the pool
     */
    public Connection get() {
        try {
            LOGGER.info("connection received from pool[%d]".formatted(pool.size()));
            return pool.take();
        } catch (InterruptedException e) {
            LOGGER.error("failed to take connection from pool. %s".formatted(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes all connections in the pool.
     */
    public void closePool() {
        try {
            for(Connection sourceConnection : sourceConnections) {
                sourceConnection.close();
            }
            LOGGER.info("all connections successfully closed");
        } catch (SQLException e) {
            LOGGER.error("failed to close all connections from pool. %s".formatted(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens a new database connection.
     *
     * @return a new Connection object
     */
    private Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertyManager.get(URL_KEY),
                    PropertyManager.get(USERNAME_KEY),
                    PropertyManager.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            LOGGER.error("failed to open connection. %s".formatted(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the connection pool with the specified size.
     */
    private void initConnectionPool() {
        String poolSize = PropertyManager.get(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        LOGGER.info("connection pool size = %s".formatted(size));
        pool = new ArrayBlockingQueue<>(size);
        sourceConnections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open();
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(
                    ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    ((proxy, method, args) -> method.getName().equals("close")
                    ? pool.add((Connection) proxy)
                            : method.invoke(connection, args)));
            pool.add(proxyConnection);
            sourceConnections.add(connection);
            LOGGER.info("connection №%d opened".formatted(i + 1));
        }
    }
}
