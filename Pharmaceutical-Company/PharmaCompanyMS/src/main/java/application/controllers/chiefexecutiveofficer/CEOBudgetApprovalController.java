package application.controllers.chiefexecutiveofficer;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CEOBudgetApprovalController extends application.controllers.DashboardBaseController {

    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, String> idCol;
    @FXML private TableColumn<Budget, String> deptCol;
    @FXML private TableColumn<Budget, Double> limitCol;
    @FXML private TableColumn<Budget, String> justCol;
    @FXML private TableColumn<Budget, String> statCol;
    @FXML private TableColumn<Budget, String> dateCol;
    @FXML private Label infoLabel;

    private static final String FILE = "budgets.bin";
    private final ObservableList<Budget> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("budgetId"));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        limitCol.setCellValueFactory(new PropertyValueFactory<>("limit"));
        justCol.setCellValueFactory(new PropertyValueFactory<>("justification"));
        statCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("decisionDate"));
        budgetTable.setItems(rows);

        statusFilter.setItems(FXCollections.observableArrayList("All", Budget.STATUS_PENDING, Budget.STATUS_APPROVED, Budget.STATUS_REJECTED));
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> loadBudgets());

        loadBudgets();
    }

    @FXML
    public void loadBudgets() {
        List<Budget> all = loadBudgetsFromBin(FILE);
        rows.clear();
        String filter = statusFilter.getValue();
        for (Budget b : all) {
            if ("All".equals(filter) || filter == null || filter.equals(b.getStatus())) {
                rows.add(b);
            }
        }
        infoLabel.setText("Showing " + rows.size() + " budget(s).");
    }

    @FXML
    private void handleApprove() {
        applyDecision(Budget.STATUS_APPROVED);
    }

    @FXML
    private void handleReject() {
        applyDecision(Budget.STATUS_REJECTED);
    }

    private void applyDecision(String newStatus) {
        Budget selected = budgetTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.warn("No selection", "Please pick a budget from the table first.");
            return;
        }
        if (!Budget.STATUS_PENDING.equals(selected.getStatus())) {
            AlertHelper.info("Already decided", "This budget has already been " + selected.getStatus() + ".");
            return;
        }
        if (selected.getBudgetId() == null || selected.getBudgetId().trim().isEmpty()) {
            AlertHelper.error("Invalid budget", "Budget record is missing an ID.");
            return;
        }

        List<Budget> all = loadBudgetsFromBin(FILE);
        for (int i = 0; i < all.size(); i++) {
            Budget b = all.get(i);
            if (b.getBudgetId().equals(selected.getBudgetId())) {
                b.setStatus(newStatus);
                b.setDecisionDate(LocalDate.now().toString());
                all.set(i, b);
                break;
            }
        }
        saveBudgetsToBin(FILE, all);
        AlertHelper.info("Saved", "Budget " + selected.getBudgetId() + " marked " + newStatus + ".");
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
