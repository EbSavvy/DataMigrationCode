package com.rubrik.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostGresHelper {
    private Connection conn;
    private String host;
    private String dbName;
    private String user;
    private String pass;

    public PostGresHelper(String host, String dbName, String user, String pass) {
        this.host = host;
        this.dbName = dbName;
        this.user = user;
        this.pass = pass;
    }

    public Connection connect() throws SQLException, ClassNotFoundException {
        if (host.isEmpty() || dbName.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            throw new SQLException("Database credentials missing");
        }

        Class.forName("org.postgresql.Driver");
        this.conn = DriverManager.getConnection(
                this.host + this.dbName,
                this.user, this.pass);
        return this.conn;
    }
}
