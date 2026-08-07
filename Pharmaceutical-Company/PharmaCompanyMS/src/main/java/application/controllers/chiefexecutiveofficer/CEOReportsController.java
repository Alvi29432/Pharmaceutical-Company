package application.controllers.chiefexecutiveofficer;

import application.model.common.FinancialRecord;
import application.model.common.Incident;
import application.utilities.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CEOReportsController extends application.controllers.DashboardBaseController {

    @FXML private Label revenueLabel;
    @FXML private Label expenseLabel;
    @FXML private Label netLabel;

    @FXML private TableView<FinancialRecord> financialTable;
    @FXML private TableColumn<FinancialRecord, String> frIdCol;
    @FXML private TableColumn<FinancialRecord, String> frTypeCol;
    @FXML private TableColumn<FinancialRecord, Double> frAmtCol;

    @FXML private TableView<Incident> auditTable;
    @FXML private TableColumn<Incident, String> incIdCol;
    @FXML private TableColumn<Incident, String> incDescCol;

    @FXML private Label infoLabel;

    private final ObservableList<FinancialRecord> financialRows = FXCollections.observableArrayList();
    private final ObservableList<Incident> auditRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        frIdCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        frTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        frAmtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        financialTable.setItems(financialRows);

        incIdCol.setCellValueFactory(new PropertyValueFactory<>("incidentId"));
        incDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        auditTable.setItems(auditRows);

        loadAll();
    }

    @FXML
    public void loadAll() {
        List<FinancialRecord> records = loadFinancialFromBin("financial.bin");
        financialRows.clear();
        financialRows.addAll(records);
        double revenue = 0.0;
        double expense = 0.0;
        for (int i = 0; i < records.size(); i++) {
            FinancialRecord r = records.get(i);
            if ("Revenue".equalsIgnoreCase(r.getType())) revenue += r.getAmount();
            else if ("Expense".equalsIgnoreCase(r.getType())) expense += r.getAmount();
            else expense += r.getAmount();        }
        revenueLabel.setText(String.format("%.2f", revenue));
        expenseLabel.setText(String.format("%.2f", expense));
        netLabel.setText(String.format("%.2f", revenue - expense));

        List<Incident> incidents = loadIncidentsFromBin("incidents.bin");
        auditRows.clear();
        auditRows.addAll(incidents);

        infoLabel.setText(records.size() + " financial record(s), " + incidents.size() + " incident(s).");
    }

    @FXML
    private void exportAuditReport() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/audit_report.txt"))) {
            pw.println("AUDIT REPORT");
            pw.println("============");
            for (int i = 0; i < auditRows.size(); i++) {
                Incident inc = auditRows.get(i);
                pw.println(inc.getIncidentId() + ": " + inc.getDescription());
            }
            AlertHelper.info("Export complete", "Audit report saved to data/audit_report.txt");
        } catch (IOException e) {
            AlertHelper.error("Export failed", e.getMessage());
        }
    }

    @FXML
    private void exportFinancialReport() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/financial_report.txt"))) {
            pw.println("FINANCIAL SUMMARY");
            pw.println("=================");
            for (int i = 0; i < financialRows.size(); i++) {
                FinancialRecord r = financialRows.get(i);
                pw.println(r.getRecordId() + " | " + r.getType() + " | " + String.format("%.2f", r.getAmount()));
            }
            pw.println();
            pw.println("Total Revenue: " + revenueLabel.getText());
            pw.println("Total Expenses: " + expenseLabel.getText());
            pw.println("Net: " + netLabel.getText());
            AlertHelper.info("Export complete", "Financial report saved to data/financial_report.txt");
        } catch (IOException e) {
            AlertHelper.error("Export failed", e.getMessage());
        }
    }

    private List<FinancialRecord> loadFinancialFromBin(String fileName) {
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

    private List<Incident> loadIncidentsFromBin(String fileName) {
        List<Incident> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Incident) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }
}
