package com.example.bank_sampah_accounting.DAO;

import com.example.bank_sampah_accounting.config.DatabaseConnection;
import com.example.bank_sampah_accounting.models.JurnalAkuntansi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JurnalAkuntansiDAO implements TransactionLedger<JurnalAkuntansi> {

    private void sinkronisasiSaldo() {
        String sql = "UPDATE tb_kas_internal SET saldo_terkini = (" +
                "COALESCE((SELECT SUM(nominal) FROM tb_jurnal_akuntansi WHERE tipe_transaksi = 'PENDAPATAN'), 0) - " +
                "COALESCE((SELECT SUM(nominal) FROM tb_jurnal_akuntansi WHERE tipe_transaksi = 'PENGELUARAN'), 0)" +
                ") WHERE id_kas = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean catatTransaksi(JurnalAkuntansi transaksi) {
        String sql = "INSERT INTO tb_jurnal_akuntansi (tipe_transaksi, nominal, keterangan, nama_user) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transaksi.getTipeTransaksi().toUpperCase());
            ps.setBigDecimal(2, transaksi.getNominal());
            ps.setString(3, transaksi.getKeterangan());
            ps.setString(4, transaksi.getNamaUser());
            ps.executeUpdate();

            sinkronisasiSaldo(); // Update saldo otomatis
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTransaksi(JurnalAkuntansi transaksi) {
        String sql = "UPDATE tb_jurnal_akuntansi SET tipe_transaksi = ?, nominal = ?, keterangan = ?, nama_user = ? WHERE id_jurnal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transaksi.getTipeTransaksi().toUpperCase());
            ps.setBigDecimal(2, transaksi.getNominal());
            ps.setString(3, transaksi.getKeterangan());
            ps.setString(4, transaksi.getNamaUser());
            ps.setInt(5, transaksi.getIdJurnal());
            ps.executeUpdate();

            sinkronisasiSaldo(); // Update saldo otomatis
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean hapusTransaksi(int idJurnal) {
        String sql = "DELETE FROM tb_jurnal_akuntansi WHERE id_jurnal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJurnal);
            ps.executeUpdate();

            sinkronisasiSaldo(); // Update saldo otomatis
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<JurnalAkuntansi> ambilSemuaTransaksi() {
        List<JurnalAkuntansi> listTransaksi = new ArrayList<>();
        String sql = "SELECT * FROM tb_jurnal_akuntansi ORDER BY tanggal ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JurnalAkuntansi jurnal = new JurnalAkuntansi();
                jurnal.setIdJurnal(rs.getInt("id_jurnal"));
                jurnal.setTanggal(rs.getTimestamp("tanggal"));
                jurnal.setTipeTransaksi(rs.getString("tipe_transaksi"));
                jurnal.setNominal(rs.getBigDecimal("nominal"));
                jurnal.setKeterangan(rs.getString("keterangan"));
                jurnal.setNamaUser(rs.getString("nama_user"));
                listTransaksi.add(jurnal);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return listTransaksi;
    }

    public java.math.BigDecimal getSaldoKasTerkini() {
        String sql = "SELECT saldo_terkini FROM tb_kas_internal WHERE id_kas = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("saldo_terkini");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return java.math.BigDecimal.ZERO;
    }
}