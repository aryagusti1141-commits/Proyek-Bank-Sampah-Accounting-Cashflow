package com.example.bank_sampah_accounting.DAO;

import java.util.List;

public interface TransactionLedger<T> {
    boolean catatTransaksi(T transaksi);
    List<T> ambilSemuaTransaksi();

    // Tambahan untuk fitur Edit dan Hapus
    boolean updateTransaksi(T transaksi);
    boolean hapusTransaksi(int id);
}