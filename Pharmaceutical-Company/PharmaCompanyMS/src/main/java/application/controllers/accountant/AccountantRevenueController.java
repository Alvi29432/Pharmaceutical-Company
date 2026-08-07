package application.controllers.accountant;

import application.model.common.FinancialRecord;
import application.utilities.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AccountantRevenueController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private Label countLabel;
    @FXML private Label infoLabel;

    @FXML private TextField sourceField;
    @FXML private TextField amountField;
    @FXML private TextField dateField;

    @FXML private TableView<FinancialRecord> revenueTable;
    @FXML private TableColumn<FinancialRecord, String>  idCol;
    @FXML private TableColumn<FinancialRecord, String>  sourceCol;
    @FXML private TableColumn<FinancialRecord, Double>  amountCol;
    @FXML private TableColumn<FinancialRecord, String>  dateCol;

    private final ObservableList<FinancialRecord> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        sourceCol.setCellValueFactory(c -> {
            FinancialRecord r = c.getValue();
            String id = r.getRecordId();
            return new javafx.beans.property.SimpleStringProperty(r.getType());
        });
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(""));
        revenueTable.setItems(rows);
        loadRevenue();
    }

    @FXML
    public void loadRevenue() {
        List<FinancialRecord> stored = loadFinancialRecordsFromBin("financial_records.bin");
        List<FinancialRecord> revenueOnly = new ArrayList<>();
        double total = 0;
        for (int i = 0; i < stored.size(); i++) {
            FinancialRecord r = stored.get(i);
            if (r.getType() != null && r.getType().startsWith("REV-")) {
                revenueOnly.add(r);
                total += r.getAmount();
            }
        }
        rows.setAll(revenueOnly);
        totalLabel.setText(String.format("%.2f", total));
        countLabel.setText(String.valueOf(revenueOnly.size()));
        infoLabel.setText("Loaded " + revenueOnly.size() + " revenue record(s).");
    }

    @FXML
    public void handleAdd() {
        String source = sourceField.getText() == null ? "" : sourceField.getText().trim();
        String amountText = amountField.getText() == null ? "" : amountField.getText().trim();
        String date = dateField.getText() == null ? "" : dateField.getText().trim();

        if ((source == null || source.trim().isEmpty()) || (amountText == null || amountText.trim().isEmpty())) {
            AlertHelper.warn("Validation", "Please fill source and amount.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                AlertHelper.warn("Validation", "Amount must be positive.");
                return;
            }
        } catch (NumberFormatException ex) {
            AlertHelper.warn("Validation", "Amount must be numeric.");
            return;
        }

        List<FinancialRecord> list = loadFinancialRecordsFromBin("financial_records.bin");
        String newId = "FR-" + String.format("%03d", list.size() + 1);
        FinancialRecord r = new FinancialRecord(newId, "REV-" + source, amount);
        list.add(r);
        saveFinancialRecordsToBin("financial_records.bin", list);

        AlertHelper.info("Saved", "Revenue " + newId + " added.");
        sourceField.clear();
        amountField.clear();
        dateField.clear();
        loadRevenue();
    }

    @FXML
    public void handleDelete() {
        FinancialRecord sel = revenueTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Selection", "Please select a revenue row to delete.");
            return;
        }
        List<FinancialRecord> list = loadFinancialRecordsFromBin("financial_records.bin");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getRecordId().equals(sel.getRecordId())) {
                list.remove(i);
                break;
            }
        }
        saveFinancialRecordsToBin("financial_records.bin", list);
        loadRevenue();
    }

    private List<FinancialRecord> loadFinancialRecordsFromBin(String filename) {
        List<FinancialRecord> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof FinancialRecord) out.add((FinancialRecord) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private void saveFinancialRecordsToBin(String filename, List<FinancialRecord> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            for (FinancialRecord item : list) oos.writeObject(item);
        } catch (IOException ignored) {}
    }
}
