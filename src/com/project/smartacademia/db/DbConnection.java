package com.project.smartacademia.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    //Singleton design patter ---> creational design pattern
    // rule 01
    private static DbConnection dbConnection;
    private Connection connection;
    // rule 02
    private DbConnection() throws SQLException, ClassNotFoundException {
        //load the driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        //Create a connection
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms3", "root", "1234");
    }
    //rule 03
    public static DbConnection getInstance() throws SQLException, ClassNotFoundException {
        if (dbConnection == null) {
            dbConnection = new DbConnection();
        }
        return dbConnection;
    }
    public Connection getConnection() {
        return connection;
    }
}


