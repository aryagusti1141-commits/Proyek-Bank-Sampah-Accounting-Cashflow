package com.example.bank_sampah_accounting;

import java.text.SimpleDateFormat;
import com.example.bank_sampah_accounting.DAO.JurnalAkuntansiDAO;
import com.example.bank_sampah_accounting.models.JurnalAkuntansi;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.util.List;

public class AccountingController {

    @FXML private TableView<JurnalAkuntansi> tableJurnal;
    @FXML private TableColumn<JurnalAkuntansi, String> colTanggal;
    @FXML private TableColumn<JurnalAkuntansi, String> colNamaUser;
    @FXML private TableColumn<JurnalAkuntansi, String> colTipe;
    @FXML private TableColumn<JurnalAkuntansi, String> colNominal;
    @FXML private TableColumn<JurnalAkuntansi, String> colKeterangan;
    @FXML private Label lblSaldoTerkini;

    @FXML private TextField txtNamaUser;
    @FXML private ComboBox<String> cmbTipe;
    @FXML private TextField txtNominal;
    @FXML private TextField txtKeterangan;
    @FXML private Button btnSimpan, btnUpdate, btnHapus;

    private JurnalAkuntansiDAO jurnalDAO;
    private JurnalAkuntansi jurnalTerpilih; // Untuk melacak baris mana yang sedang diklik

    @FXML
    public void initialize() {
        jurnalDAO = new JurnalAkuntansiDAO();
        siapkanTabel();
        muatData();

        // Listener agar form terisi saat baris tabel diklik
        tableJurnal.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && !"TOTAL".equals(newSelection.getTipeTransaksi())) {
                jurnalTerpilih = newSelection;
                txtNamaUser.setText(jurnalTerpilih.getNamaUser() != null ? jurnalTerpilih.getNamaUser() : "");
                cmbTipe.setValue(jurnalTerpilih.getTipeTransaksi());
                txtNominal.setText(jurnalTerpilih.getNominal().toString());
                txtKeterangan.setText(jurnalTerpilih.getKeterangan());

                // Matikan tombol simpan, nyalakan tombol update & hapus
                btnSimpan.setDisable(true);
                btnUpdate.setDisable(false);
                btnHapus.setDisable(false);
            }
        });
    }

    private void siapkanTabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        colTanggal.setCellValueFactory(cell -> {
            if (cell.getValue().getTanggal() != null) {
                return new SimpleStringProperty(sdf.format(cell.getValue().getTanggal()));
            }
            return new SimpleStringProperty("");
        });

        colNamaUser.setCellValueFactory(cell -> {
            if (cell.getValue().getNamaUser() != null) {
                return new SimpleStringProperty(cell.getValue().getNamaUser());
            }
            return new SimpleStringProperty("");
        });

        colTipe.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipeTransaksi()));

        colNominal.setCellValueFactory(cell -> {
            if (cell.getValue().getNominal() != null) {
                return new SimpleStringProperty("Rp " + cell.getValue().getNominal().toString());
            }
            return new SimpleStringProperty("");
        });

        colKeterangan.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKeterangan()));

        tableJurnal.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(JurnalAkuntansi item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setStyle("");
                else if ("TOTAL".equals(item.getTipeTransaksi())) setStyle("-fx-font-weight: bold; -fx-background-color: #e0e0e0;");
                else setStyle("");
            }
        });
    }
    private void muatData() {
        List<JurnalAkuntansi> listData = jurnalDAO.ambilSemuaTransaksi();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalKredit = BigDecimal.ZERO;

        for (JurnalAkuntansi j : listData) {
            if ("PENDAPATAN".equalsIgnoreCase(j.getTipeTransaksi())) totalDebit = totalDebit.add(j.getNominal());
            else if ("PENGELUARAN".equalsIgnoreCase(j.getTipeTransaksi())) totalKredit = totalKredit.add(j.getNominal());
        }

        JurnalAkuntansi barisTotal = new JurnalAkuntansi();
        barisTotal.setTipeTransaksi("TOTAL");
        barisTotal.setNominal(totalDebit.subtract(totalKredit));
        barisTotal.setKeterangan("Neraca Saldo Akhir");

        ObservableList<JurnalAkuntansi> tableData = FXCollections.observableArrayList(listData);
        tableData.add(barisTotal);
        tableJurnal.setItems(tableData);

        lblSaldoTerkini.setText("Rp " + jurnalDAO.getSaldoKasTerkini().toString());
    }

    @FXML
    private void handleSimpan() {
        try {

            if (txtNamaUser.getText().trim().isEmpty()) {
                tampilkanAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Nama User harus diisi!");
                return;
            }

            JurnalAkuntansi transaksiBaru = new JurnalAkuntansi(
                    cmbTipe.getValue(),
                    new BigDecimal(txtNominal.getText()),
                    txtKeterangan.getText()
            );

            transaksiBaru.setNamaUser(txtNamaUser.getText().trim());

            if (jurnalDAO.catatTransaksi(transaksiBaru)) {
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil disimpan!");
                clearForm();
                muatData();
            } else {
                tampilkanAlert(Alert.AlertType.ERROR, "Gagal DB", "Gagal menyimpan ke database Supabase. Cek koneksi internet.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Tampilkan pesan error merah di terminal bawah!
            tampilkanAlert(Alert.AlertType.ERROR, "Error Input", "Pastikan nominal berupa angka dan tipe transaksi tidak kosong.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (jurnalTerpilih != null) {
            try {
                if (txtNamaUser.getText().trim().isEmpty()) {
                    tampilkanAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Nama User tidak boleh kosong!");
                    return;
                }

                jurnalTerpilih.setNamaUser(txtNamaUser.getText().trim());
                jurnalTerpilih.setTipeTransaksi(cmbTipe.getValue());
                jurnalTerpilih.setNominal(new BigDecimal(txtNominal.getText()));
                jurnalTerpilih.setKeterangan(txtKeterangan.getText());

                if (jurnalDAO.updateTransaksi(jurnalTerpilih)) {
                    tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil diupdate!");
                    clearForm();
                    muatData();
                }
            } catch (Exception e) {
                tampilkanAlert(Alert.AlertType.ERROR, "Error Update", "Pastikan format input benar.");
            }
        }
    }

    @FXML
    private void handleHapus() {
        if (jurnalTerpilih != null) {
            if (jurnalDAO.hapusTransaksi(jurnalTerpilih.getIdJurnal())) {
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil dihapus!");
                clearForm();
                muatData();
            }
        }
    }

    @FXML
    private void clearForm() {
        txtNamaUser.clear();
        cmbTipe.setValue(null);
        txtNominal.clear();
        txtKeterangan.clear();
        jurnalTerpilih = null;
        btnSimpan.setDisable(false);
        btnUpdate.setDisable(true);
        btnHapus.setDisable(true);
        tableJurnal.getSelectionModel().clearSelection();
    }

    private void tampilkanAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}