package application.controllers.productionmanager;

import application.model.common.Medicine;
import application.model.common.ProductionBatch;
import application.model.common.ProductionSchedule;
import application.utilities.AlertHelper;
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

public class PMProductionPlanningController extends application.controllers.DashboardBaseController {

    @FXML private Label batchCountLabel;
    @FXML private Label scheduleCountLabel;
    @FXML private Label infoLabel;

    @FXML private ComboBox<Medicine> medicineBox;
    @FXML private Spinner<Integer> qtySpinner;
    @FXML private ComboBox<ProductionBatch> batchBox;
    @FXML private TextField dateField;

    @FXML private TableView<ProductionBatch> batchTable;
    @FXML private TableColumn<ProductionBatch, String> bIdCol;
    @FXML private TableColumn<ProductionBatch, String> bMedicineCol;
    @FXML private TableColumn<ProductionBatch, Integer> bQtyCol;
    @FXML private TableColumn<ProductionBatch, String> bStatusCol;

    @FXML private TableView<ProductionSchedule> scheduleTable;
    @FXML private TableColumn<ProductionSchedule, String> sIdCol;
    @FXML private TableColumn<ProductionSchedule, String> sBatchCol;
    @FXML private TableColumn<ProductionSchedule, String> sDateCol;

    private final ObservableList<ProductionBatch> batches = FXCollections.observableArrayList();
    private final ObservableList<ProductionSchedule> schedules = FXCollections.observableArrayList();
    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();
    private List<Medicine> medicineCache = new ArrayList<>();

    @FXML
    public void initialize() {
        qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000000, 100));

        bIdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("batchId"));
        bQtyCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("quantity"));
        bStatusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
        bMedicineCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("medicineId"));

        sIdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("scheduleId"));
        sBatchCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("batchId"));
        sDateCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("date"));

        batchTable.setItems(batches);
        scheduleTable.setItems(schedules);

        loadAll();
    }

    @FXML
    public void loadAll() {
        medicineCache = loadMedicinesFromBin("medicines.bin");
        medicines.setAll(medicineCache);

        List<ProductionBatch> storedBatches = loadBatchesFromBin("batches.bin");
        batches.setAll(storedBatches);

        List<ProductionSchedule> storedSchedules = loadSchedulesFromBin("schedules.bin");
        schedules.setAll(storedSchedules);

        batchCountLabel.setText(String.valueOf(batches.size()));
        scheduleCountLabel.setText(String.valueOf(schedules.size()));

        medicineBox.setItems(medicines);
        batchBox.setItems(batches);

        infoLabel.setText("Loaded " + batches.size() + " batch(es) and " + schedules.size() + " schedule(s).");
    }

    @FXML
    public void handleAddBatch() {
        Medicine med = medicineBox.getValue();
        if (med == null) {
            AlertHelper.warn("Validation", "Please select a medicine.");
            return;
        }
        int qty = qtySpinner.getValue();

        List<ProductionBatch> list = loadBatchesFromBin("batches.bin");
        String newId = "PB-" + String.format("%03d", list.size() + 1);
        ProductionBatch b = new ProductionBatch(newId, med.getId(), qty);
        list.add(b);
        saveBatchesToBin("batches.bin", list);

        AlertHelper.info("Saved", "Batch " + newId + " created for " + med.getName() + ".");
        loadAll();
    }

    @FXML
    public void handleAddSchedule() {
        ProductionBatch b = batchBox.getValue();
        if (b == null) {
            AlertHelper.warn("Validation", "Please select a batch.");
            return;
        }
        String date = dateField.getText() == null ? "" : dateField.getText().trim();
        if (date == null || date.trim().isEmpty()) {
            AlertHelper.warn("Validation", "Please enter a schedule date.");
            return;
        }

        List<ProductionSchedule> list = loadSchedulesFromBin("schedules.bin");
        String newId = "S-" + String.format("%03d", list.size() + 101);
        ProductionSchedule s = new ProductionSchedule(newId, b.getBatchId(), date);
        list.add(s);
        saveSchedulesToBin("schedules.bin", list);

        AlertHelper.info("Saved", "Schedule " + newId + " created for batch " + b.getBatchId() + ".");
        loadAll();
    }

    private List<Medicine> loadMedicinesFromBin(String fileName) {
        List<Medicine> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Medicine) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private List<ProductionBatch> loadBatchesFromBin(String fileName) {
        List<ProductionBatch> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((ProductionBatch) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveBatchesToBin(String fileName, List<ProductionBatch> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (ProductionBatch b : list) oos.writeObject(b);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private List<ProductionSchedule> loadSchedulesFromBin(String fileName) {
        List<ProductionSchedule> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((ProductionSchedule) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveSchedulesToBin(String fileName, List<ProductionSchedule> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (ProductionSchedule s : list) oos.writeObject(s);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
