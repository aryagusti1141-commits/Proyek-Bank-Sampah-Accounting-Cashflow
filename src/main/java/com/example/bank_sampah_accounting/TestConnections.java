package com.example.bank_sampah_accounting;

import com.example.bank_sampah_accounting.config.DatabaseConnection;
import java.sql.Connection;

public class TestConnections {
    public static void main(String[] args) {
        System.out.println("Mencoba menghubungi Supabase...");

        Connection conn = DatabaseConnection.getConnection();

        if (conn != null) {
            System.out.println("[OK]Mari lanjut ngoding OOP.");
        } else {
            System.out.println("[FAILED]Cek kembali password atau koneksi internet.");
        }
    }
}