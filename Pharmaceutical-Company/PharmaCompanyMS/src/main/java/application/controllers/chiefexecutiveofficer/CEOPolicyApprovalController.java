package application.controllers.chiefexecutiveofficer;

import application.model.chiefexecutiveofficer.Policy;
import application.utilities.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class CEOPolicyApprovalController extends application.controllers.DashboardBaseController {

    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Policy> policyTable;
    @FXML private TableColumn<Policy, String> idCol;
    @FXML private TableColumn<Policy, String> titleCol;
    @FXML private TableColumn<Policy, String> descCol;
    @FXML private TableColumn<Policy, String> byCol;
    @FXML private TableColumn<Policy, String> statCol;
    @FXML private TableColumn<Policy, String> dateCol;
    @FXML private Label infoLabel;

    private static final String FILE = "policies.bin";
    private final ObservableList<Policy> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("policyId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        byCol.setCellValueFactory(new PropertyValueFactory<>("submittedBy"));
        statCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("decisionDate"));
        policyTable.setItems(rows);

        statusFilter.setItems(FXCollections.observableArrayList("All", Policy.STATUS_PENDING, Policy.STATUS_APPROVED, Policy.STATUS_REJECTED));
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> loadPolicies());

        loadPolicies();
    }

    @FXML
    public void loadPolicies() {
        List<Policy> all = loadPoliciesFromBin(FILE);
        rows.clear();
        String filter = statusFilter.getValue();
        for (Policy p : all) {
            if ("All".equals(filter) || filter == null || filter.equals(p.getStatus())) {
                rows.add(p);
            }
        }
        infoLabel.setText("Showing " + rows.size() + " polic(ies).");
    }

    @FXML
    private void handleApprove() {
        applyDecision(Policy.STATUS_APPROVED);
    }

    @FXML
    private void handleReject() {
        applyDecision(Policy.STATUS_REJECTED);
    }

    private void applyDecision(String newStatus) {
        Policy selected = policyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.warn("No selection", "Please pick a policy from the table first.");
            return;
        }
        if (!Policy.STATUS_PENDING.equals(selected.getStatus())) {
            AlertHelper.info("Already decided", "This policy has already been " + selected.getStatus() + ".");
            return;
        }
        if (selected.getPolicyId() == null || selected.getPolicyId().trim().isEmpty()) {
            AlertHelper.error("Invalid policy", "Policy record is missing an ID.");
            return;
        }

        List<Policy> all = loadPoliciesFromBin(FILE);
        for (int i = 0; i < all.size(); i++) {
            Policy p = all.get(i);
            if (p.getPolicyId().equals(selected.getPolicyId())) {
                p.setStatus(newStatus);
                p.setDecisionDate(LocalDate.now().toString());
                all.set(i, p);
                break;
            }
        }
        savePoliciesToBin(FILE, all);
        AlertHelper.info("Saved", "Policy " + selected.getPolicyId() + " marked " + newStatus + ".");
        loadPolicies();
    }

    private List<Policy> loadPoliciesFromBin(String fileName) {
        List<Policy> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Policy) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void savePoliciesToBin(String fileName, List<Policy> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Policy p : list) oos.writeObject(p);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
