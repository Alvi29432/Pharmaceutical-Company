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

public class AccountantExpensesController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private Label countLabel;
    @FXML private Label infoLabel;

    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField descField;
    @FXML private TextField amountField;
    @FXML private TextField dateField;

    @FXML private TableView<FinancialRecord> expenseTable;
    @FXML private TableColumn<FinancialRecord, String>  idCol;
    @FXML private TableColumn<FinancialRecord, String>  categoryCol;
    @FXML private TableColumn<FinancialRecord, String>  descCol;
    @FXML private TableColumn<FinancialRecord, Double>  amountCol;
    @FXML private TableColumn<FinancialRecord, String>  dateCol;

    private final ObservableList<FinancialRecord> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        categoryBox.getItems().addAll("Supplies", "Utilities", "Salaries", "Maintenance", "Marketing", "Other");
        categoryBox.setValue("Supplies");

        idCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(parseCategory(c.getValue().getType())));
        descCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(parseDescription(c.getValue().getType())));
        dateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(""));

        expenseTable.setItems(rows);
        loadExpenses();
    }

    private String parseCategory(String type) {
        if (type == null || !type.startsWith("EXP-")) return "";
        String rest = type.substring(4);
        int dash = rest.indexOf('-');
        if (dash < 0) return rest;
        return rest.substring(0, dash);
    }

    private String parseDescription(String type) {
        if (type == null || !type.startsWith("EXP-")) return "";
        String rest = type.substring(4);
        int dash = rest.indexOf('-');
        if (dash < 0) return "";
        return rest.substring(dash + 1);
    }

    @FXML
    public void loadExpenses() {
        List<FinancialRecord> stored = loadFinancialRecordsFromBin("financial_records.bin");
        List<FinancialRecord> only = new ArrayList<>();
        double total = 0;
        for (int i = 0; i < stored.size(); i++) {
            FinancialRecord r = stored.get(i);
            if (r.getType() != null && r.getType().startsWith("EXP-")) {
                only.add(r);
                total += r.getAmount();
            }
        }
        rows.setAll(only);
        totalLabel.setText(String.format("%.2f", total));
        countLabel.setText(String.valueOf(only.size()));
        infoLabel.setText("Loaded " + only.size() + " expense record(s).");
    }

    @FXML
    public void handleAdd() {
        String category = categoryBox.getValue();
        String desc = descField.getText() == null ? "" : descField.getText().trim();
        String amountText = amountField.getText() == null ? "" : amountField.getText().trim();

        if (amountText == null || amountText.trim().isEmpty()) {
            AlertHelper.warn("Validation", "Please fill amount.");
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
        String type = "EXP-" + (category == null ? "Other" : category) + "-" + desc;
        FinancialRecord r = new FinancialRecord(newId, type, amount);
        list.add(r);
        saveFinancialRecordsToBin("financial_records.bin", list);

        AlertHelper.info("Saved", "Expense " + newId + " added.");
        descField.clear();
        amountField.clear();
        dateField.clear();
        loadExpenses();
    }

    @FXML
    public void handleDelete() {
        FinancialRecord sel = expenseTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Selection", "Please select an expense row to delete.");
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
        loadExpenses();
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
                } catch (EOFException eof) {
                    break;
                } catch (ClassNotFoundException cnf) {
                    break;
                }
            }
        } catch (IOException ignored) {
        }
        return out;
    }

    private void saveFinancialRecordsToBin(String filename, List<FinancialRecord> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            for (FinancialRecord item : list) oos.writeObject(item);
        } catch (IOException ignored) {
        }
    }
}
