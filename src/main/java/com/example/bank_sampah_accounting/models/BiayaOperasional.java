package com.example.bank_sampah_accounting.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BiayaOperasional {
    private int idBiaya;
    private Timestamp tanggal;
    private String kategoriBiaya;
    private BigDecimal nominal;
    private String keterangan;

    public BiayaOperasional() {}

    public BiayaOperasional(String kategoriBiaya, BigDecimal nominal, String keterangan) {
        this.kategoriBiaya = kategoriBiaya;
        this.nominal = nominal;
        this.keterangan = keterangan;
    }

    public int getIdBiaya() {
        return idBiaya;
    }
    public void setIdBiaya(int idBiaya) {
        this.idBiaya = idBiaya;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }
    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }

    public String getKategoriBiaya() {
        return kategoriBiaya;
    }
    public void setKategoriBiaya(String kategoriBiaya) {
        this.kategoriBiaya = kategoriBiaya;
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