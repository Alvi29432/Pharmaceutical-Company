package application.controllers.accountant;

import application.model.common.Budget;
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

public class AccountantBudgetPlanningController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private Label pendingLabel;
    @FXML private Label approvedLabel;
    @FXML private Label infoLabel;

    @FXML private TextField deptField;
    @FXML private TextField amountField;
    @FXML private TextField justField;

    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, String>  idCol;
    @FXML private TableColumn<Budget, String>  deptCol;
    @FXML private TableColumn<Budget, Double>  limitCol;
    @FXML private TableColumn<Budget, String>  justCol;
    @FXML private TableColumn<Budget, String>  statusCol;

    private final ObservableList<Budget> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("budgetId"));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        limitCol.setCellValueFactory(new PropertyValueFactory<>("limit"));
        justCol.setCellValueFactory(new PropertyValueFactory<>("justification"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        budgetTable.setItems(rows);
        loadBudgets();
    }

    @FXML
    public void loadBudgets() {
        List<Budget> stored = loadBudgetsFromBin("budgets.bin");
        rows.setAll(stored);
        double total = 0;
        int pending = 0;
        int approved = 0;
        for (int i = 0; i < stored.size(); i++) {
            Budget b = stored.get(i);
            total += b.getLimit();
            if (Budget.STATUS_APPROVED.equalsIgnoreCase(b.getStatus())) approved++;
            else if (Budget.STATUS_PENDING.equalsIgnoreCase(b.getStatus())) pending++;
        }
        totalLabel.setText(String.format("%.2f", total));
        pendingLabel.setText(String.valueOf(pending));
        approvedLabel.setText(String.valueOf(approved));
        infoLabel.setText("Loaded " + stored.size() + " budget request(s).");
    }

    @FXML
    public void handleSubmit() {
        String dept = deptField.getText() == null ? "" : deptField.getText().trim();
        String amountText = amountField.getText() == null ? "" : amountField.getText().trim();
        String just = justField.getText() == null ? "" : justField.getText().trim();

        if (dept == null || dept.trim().isEmpty() || amountText == null || amountText.trim().isEmpty()) {
            AlertHelper.warn("Validation", "Please fill department and amount.");
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

        List<Budget> list = loadBudgetsFromBin("budgets.bin");
        String newId = "B-" + String.format("%03d", list.size() + 1);
        Budget b = new Budget(newId, dept, amount, just);
        list.add(b);
        saveBudgetsToBin("budgets.bin", list);

        AlertHelper.info("Submitted", "Budget request " + newId + " submitted (Pending CEO approval).");
        deptField.clear();
        amountField.clear();
        justField.clear();
        loadBudgets();
    }

    private List<Budget> loadBudgetsFromBin(String fileName) {
        List<Budget> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Budget) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveBudgetsToBin(String fileName, List<Budget> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Budget b : list) oos.writeObject(b);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
