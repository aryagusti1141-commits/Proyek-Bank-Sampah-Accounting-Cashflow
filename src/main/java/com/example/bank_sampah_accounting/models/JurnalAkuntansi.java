package com.example.bank_sampah_accounting.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class JurnalAkuntansi {
    private int idJurnal;
    private Timestamp tanggal;
    private String tipeTransaksi;
    private BigDecimal nominal;
    private String keterangan;

    public JurnalAkuntansi() {}

    public JurnalAkuntansi(String tipeTransaksi, BigDecimal nominal, String keterangan) {
        this.tipeTransaksi = tipeTransaksi;
        this.nominal = nominal;
        this.keterangan = keterangan;
    }

    public int getIdJurnal() {
        return idJurnal;
    }
    public void setIdJurnal(int idJurnal) {
        this.idJurnal = idJurnal;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }
    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }

    public String getTipeTransaksi() {
        return tipeTransaksi;
    }
    public void setTipeTransaksi(String tipeTransaksi) {
        this.tipeTransaksi = tipeTransaksi;
    }

    public BigDecimal getNominal() {
        return nominal;
    }
    public void setNominal(BigDecimal nominal) {
        this.nominal = nominal;
    }

    public String getKeterangan() {
        return keterangan;
    }
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}