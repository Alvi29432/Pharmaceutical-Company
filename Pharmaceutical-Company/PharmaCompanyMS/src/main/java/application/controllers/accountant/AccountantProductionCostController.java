package application.controllers.accountant;

import application.model.common.Medicine;
import application.model.common.ProductionBatch;
import application.model.accountant.UnitCost;
import application.utilities.AlertHelper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AccountantProductionCostController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private Label batchLabel;
    @FXML private Label infoLabel;

    @FXML private ComboBox<Medicine> medicineBox;
    @FXML private TextField unitCostField;

    @FXML private TableView<CostRow> costTable;
    @FXML private TableColumn<CostRow, String>  batchCol;
    @FXML private TableColumn<CostRow, String>  medicineCol;
    @FXML private TableColumn<CostRow, Integer> qtyCol;
    @FXML private TableColumn<CostRow, Double>  unitCol;
    @FXML private TableColumn<CostRow, Double>  totalCol;

    private final ObservableList<CostRow> rows = FXCollections.observableArrayList();
    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        batchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().batchId));
        medicineCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().medicineName));
        qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().quantity).asObject());
        unitCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().unitCost).asObject());
        totalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().totalCost).asObject());

        costTable.setItems(rows);
        medicineBox.setItems(medicines);
        loadProductionCost();
    }

    @FXML
    public void loadProductionCost() {
        rows.clear();
        List<Medicine> meds = loadMedicinesFromBin("medicines.bin");
        medicines.setAll(meds);

        List<ProductionBatch> batches = loadBatchesFromBin("batches.bin");
        List<UnitCost> overrides = loadUnitCostsFromBin("unit_costs.bin");
        double total = 0;
        for (int i = 0; i < batches.size(); i++) {
            ProductionBatch b = batches.get(i);
            String medName = b.getMedicineId();
            double price = 0;
            for (int j = 0; j < meds.size(); j++) {
                if (meds.get(j).getId().equals(b.getMedicineId())) {
                    medName = meds.get(j).getName();
                    price = meds.get(j).getPrice();
                    break;
                }
            }
            double unit = price * 0.4;
            for (int j = 0; j < overrides.size(); j++) {
                if (overrides.get(j).getMedicineId().equals(b.getMedicineId())) {
                    unit = overrides.get(j).getUnitCost();
                    break;
                }
            }
            double totalCost = unit * b.getQuantity();
            total += totalCost;
            rows.add(new CostRow(b.getBatchId(), medName, b.getQuantity(), unit, totalCost));
        }
        totalLabel.setText(String.format("%.2f", total));
        batchLabel.setText(String.valueOf(batches.size()));
        infoLabel.setText("Loaded " + rows.size() + " cost row(s).");
    }

    @FXML
    public void handleSaveUnitCost() {
        Medicine m = medicineBox.getValue();
        String amountText = unitCostField.getText() == null ? "" : unitCostField.getText().trim();
        if (m == null || (amountText == null || amountText.trim().isEmpty())) {
            AlertHelper.warn("Validation", "Please select a medicine and enter a unit cost.");
            return;
        }
        double cost;
        try {
            cost = Double.parseDouble(amountText);
            if (cost < 0) {
                AlertHelper.warn("Validation", "Unit cost must be non-negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            AlertHelper.warn("Validation", "Unit cost must be numeric.");
            return;
        }
        List<UnitCost> list = loadUnitCostsFromBin("unit_costs.bin");
        boolean updated = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMedicineId().equals(m.getId())) {
                list.get(i).setUnitCost(cost);
                updated = true;
                break;
            }
        }
        if (!updated) list.add(new UnitCost(m.getId(), cost));
        saveUnitCostsToBin("unit_costs.bin", list);
        AlertHelper.info("Saved", "Unit cost saved for " + m.getName() + ".");
        medicineBox.setValue(null);
        unitCostField.clear();
        loadProductionCost();
    }

    private List<Medicine> loadMedicinesFromBin(String filename) {
        List<Medicine> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof Medicine) out.add((Medicine) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private List<ProductionBatch> loadBatchesFromBin(String filename) {
        List<ProductionBatch> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof ProductionBatch) out.add((ProductionBatch) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private List<UnitCost> loadUnitCostsFromBin(String filename) {
        List<UnitCost> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof UnitCost) out.add((UnitCost) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private void saveUnitCostsToBin(String filename, List<UnitCost> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            for (UnitCost item : list) oos.writeObject(item);
        } catch (IOException ignored) {}
    }

    public static class CostRow {
        public final String batchId;
        public final String medicineName;
        public final int quantity;
        public final double unitCost;
        public final double totalCost;
        public CostRow(String batchId, String medicineName, int quantity, double unitCost, double totalCost) {
            this.batchId = batchId;
            this.medicineName = medicineName;
            this.quantity = quantity;
            this.unitCost = unitCost;
            this.totalCost = totalCost;
        }
    }
}
