package com.trinova.scms.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:sqlserver://localhost\\SQLEXPRESS;" +
        "databaseName=CoworkingSpace_db;" +
        "encrypt=true;" +
        "trustServerCertificate=true;";

    private static final String USER = "sa";
    private static final String PASS = "Scms@1234";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASS);
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
