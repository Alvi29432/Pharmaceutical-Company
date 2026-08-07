package application.controllers.productionmanager;

import application.model.productionmanager.BatchProgress;
import application.model.common.Medicine;
import application.model.common.ProductionBatch;
import application.model.productionmanager.QualityInspection;
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

public class PMReportsController extends application.controllers.DashboardBaseController {

    @FXML private Label totalBatchesLabel;
    @FXML private Label targetUnitsLabel;
    @FXML private Label completedUnitsLabel;
    @FXML private Label overallLabel;
    @FXML private Label defectedLabel;
    @FXML private Label infoLabel;

    @FXML private TableView<ReportRow> reportTable;
    @FXML private TableColumn<ReportRow, String>  batchCol;
    @FXML private TableColumn<ReportRow, String>  medicineCol;
    @FXML private TableColumn<ReportRow, Integer> qtyCol;
    @FXML private TableColumn<ReportRow, Integer> completedCol;
    @FXML private TableColumn<ReportRow, Integer> percentCol;
    @FXML private TableColumn<ReportRow, String>  statusCol;

    private final ObservableList<ReportRow> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        batchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().batchId));
        medicineCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().medicineName));
        qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().targetQty).asObject());
        completedCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().completedUnits).asObject());
        percentCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().percent).asObject());
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status));

        reportTable.setItems(rows);
        loadReports();
    }

    @FXML
    public void loadReports() {
        rows.clear();
        List<ProductionBatch> batches = loadBatchesFromBin("batches.bin");
        List<BatchProgress> progress = loadBatchProgressFromBin("batch_progress.bin");
        List<Medicine> meds = loadMedicinesFromBin("medicines.bin");
        List<QualityInspection> inspections = loadInspectionsFromBin("quality_inspections.bin");

        int targetTotal = 0;
        int completedTotal = 0;

        for (int i = 0; i < batches.size(); i++) {
            ProductionBatch b = batches.get(i);
            String medName = b.getMedicineId();
            for (int j = 0; j < meds.size(); j++) {
                if (meds.get(j).getId().equals(b.getMedicineId())) {
                    medName = meds.get(j).getName();
                    break;
                }
            }

            int units = 0;
            int pct = 0;
            for (int j = 0; j < progress.size(); j++) {
                if (progress.get(j).getBatchId().equals(b.getBatchId())) {
                    units = progress.get(j).getCompletedUnits();
                    pct = progress.get(j).getPercent();
                    break;
                }
            }

            String status;
            if (pct <= 0) status = "Planned";
            else if (pct >= 100) status = "Completed";
            else status = "In Progress";

            targetTotal += b.getQuantity();
            completedTotal += units;

            rows.add(new ReportRow(b.getBatchId(), medName, b.getQuantity(), units, pct, status));
        }

        int overall = targetTotal == 0 ? 0 : (completedTotal * 100) / targetTotal;
        int defected = 0;
        for (int i = 0; i < inspections.size(); i++) {
            String r = inspections.get(i).getResult();
            if (r != null && r.equalsIgnoreCase("Fail")) defected++;
        }

        totalBatchesLabel.setText(String.valueOf(batches.size()));
        targetUnitsLabel.setText(String.valueOf(targetTotal));
        completedUnitsLabel.setText(String.valueOf(completedTotal));
        overallLabel.setText(overall + "%");
        defectedLabel.setText(String.valueOf(defected));

        infoLabel.setText("Loaded " + rows.size() + " report row(s).");
    }

    public static class ReportRow {
        public final String batchId;
        public final String medicineName;
        public final int targetQty;
        public final int completedUnits;
        public final int percent;
        public final String status;

        public ReportRow(String batchId, String medicineName, int targetQty,
                         int completedUnits, int percent, String status) {
            this.batchId = batchId;
            this.medicineName = medicineName;
            this.targetQty = targetQty;
            this.completedUnits = completedUnits;
            this.percent = percent;
            this.status = status;
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

    private List<QualityInspection> loadInspectionsFromBin(String fileName) {
        List<QualityInspection> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((QualityInspection) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }
}
