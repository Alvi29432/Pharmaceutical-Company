package application.controllers.accountant;

import application.model.accountant.Payable;
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

public class AccountantAccountsPayableController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private Label outstandingLabel;
    @FXML private Label paidLabel;
    @FXML private Label infoLabel;

    @FXML private TextField vendorField;
    @FXML private TextField amountField;
    @FXML private TextField dueField;

    @FXML private TableView<Payable> payableTable;
    @FXML private TableColumn<Payable, String>  idCol;
    @FXML private TableColumn<Payable, String>  vendorCol;
    @FXML private TableColumn<Payable, Double>  amountCol;
    @FXML private TableColumn<Payable, String>  dueCol;
    @FXML private TableColumn<Payable, String>  statusCol;

    private final ObservableList<Payable> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("payableId"));
        vendorCol.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dueCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        payableTable.setItems(rows);
        loadPayables();
    }

    @FXML
    public void loadPayables() {
        List<Payable> stored = loadPayablesFromBin("accounts_payable.bin");
        rows.setAll(stored);
        double total = 0;
        int open = 0;
        int paid = 0;
        for (int i = 0; i < stored.size(); i++) {
            Payable p = stored.get(i);
            if ("Open".equalsIgnoreCase(p.getStatus())) {
                total += p.getAmount();
                open++;
            } else {
                paid++;
            }
        }
        totalLabel.setText(String.format("%.2f", total));
        outstandingLabel.setText(String.valueOf(open));
        paidLabel.setText(String.valueOf(paid));
        infoLabel.setText("Loaded " + stored.size() + " payable record(s).");
    }

    @FXML
    public void handleAdd() {
        String vendor = vendorField.getText() == null ? "" : vendorField.getText().trim();
        String amountText = amountField.getText() == null ? "" : amountField.getText().trim();
        String due = dueField.getText() == null ? "" : dueField.getText().trim();

        if (vendor == null || vendor.trim().isEmpty() || amountText == null || amountText.trim().isEmpty()) {
            AlertHelper.warn("Validation", "Please fill vendor and amount.");
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

        List<Payable> list = loadPayablesFromBin("accounts_payable.bin");
        String newId = "AP-" + String.format("%03d", list.size() + 1);
        Payable p = new Payable(newId, vendor, amount, due, "Open");
        list.add(p);
        savePayablesToBin("accounts_payable.bin", list);

        AlertHelper.info("Saved", "Payable " + newId + " created.");
        vendorField.clear();
        amountField.clear();
        dueField.clear();
        loadPayables();
    }

    @FXML
    public void handleMarkPaid() {
        Payable sel = payableTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Selection", "Please select a payable to mark as paid.");
            return;
        }
        List<Payable> list = loadPayablesFromBin("accounts_payable.bin");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPayableId().equals(sel.getPayableId())) {
                list.get(i).setStatus("Paid");
                break;
            }
        }
        savePayablesToBin("accounts_payable.bin", list);
        loadPayables();
    }

    @FXML
    public void handleDelete() {
        Payable sel = payableTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Selection", "Please select a payable to delete.");
            return;
        }
        List<Payable> list = loadPayablesFromBin("accounts_payable.bin");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPayableId().equals(sel.getPayableId())) {
                list.remove(i);
                break;
            }
        }
        savePayablesToBin("accounts_payable.bin", list);
        loadPayables();
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

    private void savePayablesToBin(String fileName, List<Payable> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Payable p : list) oos.writeObject(p);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
