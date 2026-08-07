package application.controllers.productionmanager;

import application.model.productionmanager.BatchProgress;
import application.model.common.Medicine;
import application.model.common.ProductionBatch;
import application.model.common.ProductionSchedule;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class PMProductionStatusController extends application.controllers.DashboardBaseController {

    @FXML private Label plannedLabel;
    @FXML private Label inProgressLabel;
    @FXML private Label completedLabel;
    @FXML private Label infoLabel;

    @FXML private TableView<StatusRow> statusTable;
    @FXML private TableColumn<StatusRow, String>  batchCol;
    @FXML private TableColumn<StatusRow, String>  medicineCol;
    @FXML private TableColumn<StatusRow, Integer> qtyCol;
    @FXML private TableColumn<StatusRow, String>  statusCol;
    @FXML private TableColumn<StatusRow, String>  scheduleCol;

    private final ObservableList<StatusRow> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        batchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().batchId));
        medicineCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().medicineName));
        qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().quantity).asObject());
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status));
        scheduleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().scheduleDate));

        statusTable.setItems(rows);
        loadStatus();
    }

    @FXML
    public void loadStatus() {
        rows.clear();
        List<ProductionBatch> batches = loadBatchesFromBin("batches.bin");
        List<ProductionSchedule> schedules = loadSchedulesFromBin("schedules.bin");
        List<BatchProgress> progress = loadBatchProgressFromBin("batch_progress.bin");
        List<Medicine> meds = loadMedicinesFromBin("medicines.bin");

        int planned = 0, inProgress = 0, completed = 0;

        for (int i = 0; i < batches.size(); i++) {
            ProductionBatch b = batches.get(i);
            String medName = b.getMedicineId();
            for (int j = 0; j < meds.size(); j++) {
                if (meds.get(j).getId().equals(b.getMedicineId())) {
                    medName = meds.get(j).getName();
                    break;
                }
            }
            String date = "(unscheduled)";
            for (int j = 0; j < schedules.size(); j++) {
                if (schedules.get(j).getBatchId().equals(b.getBatchId())) {
                    date = schedules.get(j).getDate();
                    break;
                }
            }
            int pct = 0;
            for (int j = 0; j < progress.size(); j++) {
                if (progress.get(j).getBatchId().equals(b.getBatchId())) {
                    pct = progress.get(j).getPercent();
                    break;
                }
            }
            String derived;
            if (pct <= 0) { derived = "Planned"; planned++; }
            else if (pct >= 100) { derived = "Completed"; completed++; }
            else { derived = "In Progress"; inProgress++; }

            rows.add(new StatusRow(b.getBatchId(), medName, b.getQuantity(), derived, date));
        }

        plannedLabel.setText(String.valueOf(planned));
        inProgressLabel.setText(String.valueOf(inProgress));
        completedLabel.setText(String.valueOf(completed));

        infoLabel.setText("Loaded " + rows.size() + " status row(s).");
    }

    public static class StatusRow {
        public final String batchId;
        public final String medicineName;
        public final int quantity;
        public final String status;
        public final String scheduleDate;

        public StatusRow(String batchId, String medicineName, int quantity, String status, String scheduleDate) {
            this.batchId = batchId;
            this.medicineName = medicineName;
            this.quantity = quantity;
            this.status = status;
            this.scheduleDate = scheduleDate;
        }
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

    private List<BatchProgress> loadBatchProgressFromBin(String fileName) {
        List<BatchProgress> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((BatchProgress) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
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
}
