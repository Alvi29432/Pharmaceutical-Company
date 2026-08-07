package application.controllers.chiefexecutiveofficer;

import application.model.common.ProductionBatch;
import application.model.common.ProductionSchedule;
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

public class CEOProductionController extends application.controllers.DashboardBaseController {

    @FXML private TableView<ProductionBatch> batchTable;
    @FXML private TableColumn<ProductionBatch, String> batchIdCol;
    @FXML private TableColumn<ProductionBatch, String> batchMedCol;
    @FXML private TableColumn<ProductionBatch, Integer> batchQtyCol;
    @FXML private TableColumn<ProductionBatch, String> batchStatCol;

    @FXML private TableView<ProductionSchedule> scheduleTable;
    @FXML private TableColumn<ProductionSchedule, String> schedIdCol;
    @FXML private TableColumn<ProductionSchedule, String> schedBatchCol;
    @FXML private TableColumn<ProductionSchedule, String> schedDateCol;

    @FXML private Label infoLabel;

    private static final String BATCHES_FILE = "batches.bin";
    private static final String SCHEDULES_FILE = "schedules.bin";

    private final ObservableList<ProductionBatch> batchRows = FXCollections.observableArrayList();
    private final ObservableList<ProductionSchedule> scheduleRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        batchIdCol.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        batchMedCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        batchQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        batchStatCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        batchTable.setItems(batchRows);

        schedIdCol.setCellValueFactory(new PropertyValueFactory<>("scheduleId"));
        schedBatchCol.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        schedDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        scheduleTable.setItems(scheduleRows);

        loadAll();
    }

    @FXML
    public void loadAll() {
        batchRows.clear();
        batchRows.addAll(loadBatchesFromBin(BATCHES_FILE));

        scheduleRows.clear();
        scheduleRows.addAll(loadSchedulesFromBin(SCHEDULES_FILE));

        infoLabel.setText(batchRows.size() + " batch(es), " + scheduleRows.size() + " schedule(s).");
    }

    @FXML
    private void handleStart() {
        updateSelectedBatchStatus(ProductionBatch.STATUS_IN_PROGRESS);
    }

    @FXML
    private void handleComplete() {
        updateSelectedBatchStatus(ProductionBatch.STATUS_COMPLETED);
    }

    private void updateSelectedBatchStatus(String newStatus) {
        ProductionBatch selected = batchTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.warn("No selection", "Please pick a batch first.");
            return;
        }
        List<ProductionBatch> all = loadBatchesFromBin(BATCHES_FILE);
        for (int i = 0; i < all.size(); i++) {
            ProductionBatch b = all.get(i);
            if (b.getBatchId().equals(selected.getBatchId())) {
                b.setStatus(newStatus);
                all.set(i, b);
                break;
            }
        }
        saveBatchesToBin(BATCHES_FILE, all);
        loadAll();
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

    private void saveBatchesToBin(String fileName, List<ProductionBatch> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (ProductionBatch b : list) oos.writeObject(b);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
