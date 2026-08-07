package application.controllers.accountant;

import application.model.accountant.Receivable;
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

public class AccountantAccountsReceivableController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private Label openLabel;
    @FXML private Label collectedLabel;
    @FXML private Label infoLabel;

    @FXML private TextField customerField;
    @FXML private TextField amountField;
    @FXML private TextField dueField;

    @FXML private TableView<Receivable> receivableTable;
    @FXML private TableColumn<Receivable, String>  idCol;
    @FXML private TableColumn<Receivable, String>  customerCol;
    @FXML private TableColumn<Receivable, Double>  amountCol;
    @FXML private TableColumn<Receivable, String>  dueCol;
    @FXML private TableColumn<Receivable, String>  statusCol;

    private final ObservableList<Receivable> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("receivableId"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customer"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dueCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        receivableTable.setItems(rows);
        loadReceivables();
    }

    @FXML
    public void loadReceivables() {
        List<Receivable> stored = loadReceivablesFromBin("accounts_receivable.bin");
        rows.setAll(stored);
        double total = 0;
        int open = 0;
        int collected = 0;
        for (int i = 0; i < stored.size(); i++) {
            Receivable r = stored.get(i);
            if ("Open".equalsIgnoreCase(r.getStatus())) {
                total += r.getAmount();
                open++;
            } else {
                collected++;
            }
        }
        totalLabel.setText(String.format("%.2f", total));
        openLabel.setText(String.valueOf(open));
        collectedLabel.setText(String.valueOf(collected));
        infoLabel.setText("Loaded " + stored.size() + " receivable record(s).");
    }

    @FXML
    public void handleAdd() {
        String customer = customerField.getText() == null ? "" : customerField.getText().trim();
        String amountText = amountField.getText() == null ? "" : amountField.getText().trim();
        String due = dueField.getText() == null ? "" : dueField.getText().trim();

        if (customer == null || customer.trim().isEmpty() || amountText == null || amountText.trim().isEmpty()) {
            AlertHelper.warn("Validation", "Please fill customer and amount.");
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

        List<Receivable> list = loadReceivablesFromBin("accounts_receivable.bin");
        String newId = "AR-" + String.format("%03d", list.size() + 1);
        Receivable r = new Receivable(newId, customer, amount, due, "Open");
        list.add(r);
        saveReceivablesToBin("accounts_receivable.bin", list);

        AlertHelper.info("Saved", "Receivable " + newId + " created.");
        customerField.clear();
        amountField.clear();
        dueField.clear();
        loadReceivables();
    }

    @FXML
    public void handleMarkCollected() {
        Receivable sel = receivableTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Selection", "Please select a receivable to mark as collected.");
            return;
        }
        List<Receivable> list = loadReceivablesFromBin("accounts_receivable.bin");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getReceivableId().equals(sel.getReceivableId())) {
                list.get(i).setStatus("Collected");
                break;
            }
        }
        saveReceivablesToBin("accounts_receivable.bin", list);
        loadReceivables();
    }

    @FXML
    public void handleDelete() {
        Receivable sel = receivableTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Selection", "Please select a receivable to delete.");
            return;
        }
        List<Receivable> list = loadReceivablesFromBin("accounts_receivable.bin");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getReceivableId().equals(sel.getReceivableId())) {
                list.remove(i);
                break;
            }
        }
        saveReceivablesToBin("accounts_receivable.bin", list);
        loadReceivables();
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

    private void saveReceivablesToBin(String fileName, List<Receivable> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Receivable r : list) oos.writeObject(r);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
