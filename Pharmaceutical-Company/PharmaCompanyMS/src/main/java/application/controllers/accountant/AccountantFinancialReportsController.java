package application.controllers.accountant;

import application.model.accountant.ComplianceRecord;
import application.model.common.FinancialRecord;
import application.model.accountant.Payable;
import application.model.accountant.Receivable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class AccountantFinancialReportsController extends application.controllers.DashboardBaseController {

    @FXML private Label revenueLabel;
    @FXML private Label expenseLabel;
    @FXML private Label profitLabel;
    @FXML private Label marginLabel;
    @FXML private Label payablesLabel;
    @FXML private Label receivablesLabel;
    @FXML private Label complianceLabel;
    @FXML private Label infoLabel;

    @FXML private TableView<FinancialRecord> reportTable;
    @FXML private TableColumn<FinancialRecord, String>  idCol;
    @FXML private TableColumn<FinancialRecord, String>  typeCol;
    @FXML private TableColumn<FinancialRecord, Double>  amountCol;
    @FXML private TableColumn<FinancialRecord, String>  dateCol;

    private final ObservableList<FinancialRecord> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(""));
        reportTable.setItems(rows);
        loadReports();
    }

    @FXML
    public void loadReports() {
        List<FinancialRecord> all = loadFinancialRecordsFromBin("financial_records.bin");
        double revenue = 0;
        double expense = 0;
        for (int i = 0; i < all.size(); i++) {
            FinancialRecord r = all.get(i);
            if (r.getType() == null) continue;
            if (r.getType().startsWith("REV-")) revenue += r.getAmount();
            else if (r.getType().startsWith("EXP-")) expense += r.getAmount();
        }
        double profit = revenue - expense;
        double margin = revenue == 0 ? 0 : (profit * 100.0) / revenue;

        List<Payable> payables = loadPayablesFromBin("accounts_payable.bin");
        double totalPayables = 0;
        for (int i = 0; i < payables.size(); i++) {
            Payable p = payables.get(i);
            if ("Open".equalsIgnoreCase(p.getStatus())) totalPayables += p.getAmount();
        }
        List<Receivable> receivables = loadReceivablesFromBin("accounts_receivable.bin");
        double totalReceivables = 0;
        for (int i = 0; i < receivables.size(); i++) {
            Receivable r = receivables.get(i);
            if ("Open".equalsIgnoreCase(r.getStatus())) totalReceivables += r.getAmount();
        }
        List<ComplianceRecord> compliance = loadComplianceFromBin("compliance.bin");
        int openCompliance = 0;
        for (int i = 0; i < compliance.size(); i++) {
            ComplianceRecord c = compliance.get(i);
            if ("Open".equalsIgnoreCase(c.getStatus())) openCompliance++;
        }

        revenueLabel.setText(String.format("%.2f", revenue));
        expenseLabel.setText(String.format("%.2f", expense));
        profitLabel.setText(String.format("%.2f", profit));
        marginLabel.setText(String.format("%.1f%%", margin));
        payablesLabel.setText(String.format("%.2f", totalPayables));
        receivablesLabel.setText(String.format("%.2f", totalReceivables));
        complianceLabel.setText(String.valueOf(openCompliance));

        rows.setAll(all);
        infoLabel.setText("Loaded " + all.size() + " financial record(s).");
    }

    private List<FinancialRecord> loadFinancialRecordsFromBin(String fileName) {
        List<FinancialRecord> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((FinancialRecord) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private List<Payable> loadPayablesFromBin(String fileName) {
        List<Payable> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Payable) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private List<Receivable> loadReceivablesFromBin(String fileName) {
        List<Receivable> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Receivable) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private List<ComplianceRecord> loadComplianceFromBin(String fileName) {
        List<ComplianceRecord> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((ComplianceRecord) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }
}
