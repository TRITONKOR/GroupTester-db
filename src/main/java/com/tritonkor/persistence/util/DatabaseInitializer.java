package com.tritonkor.persistence.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Initializes the database by executing SQL scripts.
 * This class reads SQL scripts from the resources and executes them to set up the database schema and data.
 */
@Component
public final class DatabaseInitializer {
    private final ConnectionManager connectionManager;

    /**
     * Constructs a DatabaseInitializer with the specified ConnectionManager.
     *
     * @param connectionManager the ConnectionManager to obtain database connections
     */
    public DatabaseInitializer(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Initializes the database by executing DDL and DML scripts.
     */
    public void init() {

        try (Connection connection = connectionManager.get();
                Statement statementForDDL = connection.createStatement();
                Statement statementForDML = connection.createStatement()) {
            statementForDDL.execute(getSQL("ddl.sql"));
            statementForDML.execute(getSQL("dml.sql"));

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
            throw new RuntimeException(throwables);
        }
    }

    /**
     * Reads the SQL script from the specified resource file.
     *
     * @param resourceName the name of the resource file containing the SQL script
     * @return the SQL script as a string
     */
    private String getSQL(final String resourceName) {
        return new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                ConnectionManager.class.getClassLoader().getResourceAsStream(resourceName)))
        ).lines().collect(Collectors.joining("\n"));
    }

}
