package com.example.bank_sampah_accounting.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    private static final String URL = "jdbc:postgresql://db.ctcuqmnrcczinnqenvuo.supabase.co:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "MASUKAN_PASSWORD_DISINI";

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Gagal terkoneksi ke database!");
            e.printStackTrace();
        }
        return connection;
    }
}
