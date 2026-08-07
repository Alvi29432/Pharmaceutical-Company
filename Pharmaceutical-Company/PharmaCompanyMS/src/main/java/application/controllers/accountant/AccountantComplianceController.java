package application.controllers.accountant;

import application.model.accountant.ComplianceRecord;
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

public class AccountantComplianceController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private Label openLabel;
    @FXML private Label resolvedLabel;
    @FXML private Label infoLabel;

    @FXML private TextField regulationField;
    @FXML private TextField descField;

    @FXML private TableView<ComplianceRecord> complianceTable;
    @FXML private TableColumn<ComplianceRecord, String> idCol;
    @FXML private TableColumn<ComplianceRecord, String> regulationCol;
    @FXML private TableColumn<ComplianceRecord, String> descCol;
    @FXML private TableColumn<ComplianceRecord, String> statusCol;

    private final ObservableList<ComplianceRecord> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        regulationCol.setCellValueFactory(new PropertyValueFactory<>("regulation"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        complianceTable.setItems(rows);
        loadCompliance();
    }

    @FXML
    public void loadCompliance() {
        List<ComplianceRecord> stored = loadComplianceFromBin("compliance.bin");
        rows.setAll(stored);
        int open = 0;
        int resolved = 0;
        for (int i = 0; i < stored.size(); i++) {
            if ("Resolved".equalsIgnoreCase(stored.get(i).getStatus())) resolved++;
            else open++;
        }
        totalLabel.setText(String.valueOf(stored.size()));
        openLabel.setText(String.valueOf(open));
        resolvedLabel.setText(String.valueOf(resolved));
        infoLabel.setText("Loaded " + stored.size() + " compliance record(s).");
    }

    @FXML
    public void handleAdd() {
        String regulation = regulationField.getText() == null ? "" : regulationField.getText().trim();
        String desc = descField.getText() == null ? "" : descField.getText().trim();
        if ((regulation == null || regulation.trim().isEmpty()) || (desc == null || desc.trim().isEmpty())) {
            AlertHelper.warn("Validation", "Please fill regulation and description.");
            return;
        }

        List<ComplianceRecord> list = loadComplianceFromBin("compliance.bin");
        String newId = "CR-" + String.format("%03d", list.size() + 1);
        ComplianceRecord r = new ComplianceRecord(newId, regulation, desc, "Open");
        list.add(r);
        saveComplianceToBin("compliance.bin", list);
        AlertHelper.info("Saved", "Compliance record " + newId + " added.");
        regulationField.clear();
        descField.clear();
        loadCompliance();
    }

    @FXML
    public void handleResolve() {
        ComplianceRecord sel = complianceTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Selection", "Please select a record to resolve.");
            return;
        }
        List<ComplianceRecord> list = loadComplianceFromBin("compliance.bin");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getRecordId().equals(sel.getRecordId())) {
                list.get(i).setStatus("Resolved");
                break;
            }
        }
        saveComplianceToBin("compliance.bin", list);
        loadCompliance();
    }

    @FXML
    public void handleDelete() {
        ComplianceRecord sel = complianceTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Selection", "Please select a record to delete.");
            return;
        }
        List<ComplianceRecord> list = loadComplianceFromBin("compliance.bin");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getRecordId().equals(sel.getRecordId())) {
                list.remove(i);
                break;
            }
        }
        saveComplianceToBin("compliance.bin", list);
        loadCompliance();
    }

    private List<ComplianceRecord> loadComplianceFromBin(String filename) {
        List<ComplianceRecord> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof ComplianceRecord) out.add((ComplianceRecord) obj);
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

    private void saveComplianceToBin(String filename, List<ComplianceRecord> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            for (ComplianceRecord item : list) oos.writeObject(item);
        } catch (IOException ignored) {
        }
    }
}
