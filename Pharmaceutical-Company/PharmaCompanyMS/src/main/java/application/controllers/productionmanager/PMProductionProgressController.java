package application.controllers.productionmanager;

import application.model.productionmanager.BatchProgress;
import application.model.common.ProductionBatch;
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

public class PMProductionProgressController extends application.controllers.DashboardBaseController {

    @FXML private Label avgPercentLabel;
    @FXML private Label totalUnitsLabel;
    @FXML private Label infoLabel;

    @FXML private ComboBox<ProductionBatch> batchBox;
    @FXML private Spinner<Integer> unitsSpinner;
    @FXML private Spinner<Integer> percentSpinner;

    @FXML private TableView<BatchProgress> progressTable;
    @FXML private TableColumn<BatchProgress, String>  pBatchCol;
    @FXML private TableColumn<BatchProgress, Integer> pUnitsCol;
    @FXML private TableColumn<BatchProgress, Integer> pPercentCol;
    @FXML private TableColumn<BatchProgress, String>  pStatusCol;

    private final ObservableList<BatchProgress> rows = FXCollections.observableArrayList();
    private final ObservableList<ProductionBatch> batches = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        unitsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000000, 0));
        percentSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));

        pBatchCol.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        pUnitsCol.setCellValueFactory(new PropertyValueFactory<>("completedUnits"));
        pPercentCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getPercent()).asObject());
        pStatusCol.setCellValueFactory(c -> {
            int p = c.getValue().getPercent();
            String s;
            if (p <= 0) s = "Not started";
            else if (p >= 100) s = "Complete";
            else s = "In progress";
            return new javafx.beans.property.SimpleStringProperty(s);
        });

        progressTable.setItems(rows);
        batchBox.setItems(batches);

        loadProgress();
    }

    @FXML
    public void loadProgress() {
        List<BatchProgress> stored = loadBatchProgressFromBin("batch_progress.bin");
        rows.setAll(stored);

        List<ProductionBatch> allBatches = loadBatchesFromBin("batches.bin");
        batches.setAll(allBatches);

        for (int i = 0; i < allBatches.size(); i++) {
            ProductionBatch b = allBatches.get(i);
            boolean found = false;
            for (int j = 0; j < stored.size(); j++) {
                if (stored.get(j).getBatchId().equals(b.getBatchId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                BatchProgress bp = new BatchProgress(b.getBatchId(), 0, 0);
                rows.add(bp);
                stored.add(bp);
            }
        }
        saveBatchProgressToBin("batch_progress.bin", stored);

        int totalUnits = 0;
        int pctSum = 0;
        for (int i = 0; i < rows.size(); i++) {
            BatchProgress r = rows.get(i);
            totalUnits += r.getCompletedUnits();
            pctSum += r.getPercent();
        }
        int avg = rows.isEmpty() ? 0 : pctSum / rows.size();
        avgPercentLabel.setText(avg + "%");
        totalUnitsLabel.setText(String.valueOf(totalUnits));

        infoLabel.setText("Loaded " + rows.size() + " progress record(s).");
    }

    @FXML
    public void handleSave() {
        ProductionBatch b = batchBox.getValue();
        if (b == null) {
            AlertHelper.warn("Validation", "Please select a batch.");
            return;
        }
        int units = unitsSpinner.getValue();
        int pct = percentSpinner.getValue();

        List<BatchProgress> stored = loadBatchProgressFromBin("batch_progress.bin");
        boolean updated = false;
        for (int i = 0; i < stored.size(); i++) {
            BatchProgress r = stored.get(i);
            if (r.getBatchId().equals(b.getBatchId())) {
                r.setCompletedUnits(units);
                r.setPercent(pct);
                updated = true;
                break;
            }
        }
        if (!updated) {
            stored.add(new BatchProgress(b.getBatchId(), units, pct));
        }
        saveBatchProgressToBin("batch_progress.bin", stored);

        AlertHelper.info("Saved", "Progress saved for batch " + b.getBatchId() + ".");
        loadProgress();
    }

    @FXML
    public void handleMarkInProgress() {
        percentSpinner.getValueFactory().setValue(50);
        unitsSpinner.getValueFactory().setValue(Math.max(unitsSpinner.getValue(), 1));
        handleSave();
    }

    @FXML
    public void handleMarkCompleted() {
        percentSpinner.getValueFactory().setValue(100);
        handleSave();
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

    private void saveBatchProgressToBin(String fileName, List<BatchProgress> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (BatchProgress b : list) oos.writeObject(b);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
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
}
