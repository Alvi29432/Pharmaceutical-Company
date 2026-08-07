package application.controllers.productionmanager;

import application.model.common.ProductionBatch;
import application.model.productionmanager.QualityInspection;
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

public class PMQualityInspectionController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private Label passLabel;
    @FXML private Label failLabel;
    @FXML private Label infoLabel;

    @FXML private ComboBox<ProductionBatch> batchBox;
    @FXML private ComboBox<String> resultBox;

    @FXML private TableView<QualityInspection> inspectionTable;
    @FXML private TableColumn<QualityInspection, String> idCol;
    @FXML private TableColumn<QualityInspection, String> batchCol;
    @FXML private TableColumn<QualityInspection, String> resultCol;

    private final ObservableList<QualityInspection> rows = FXCollections.observableArrayList();
    private final ObservableList<ProductionBatch> batches = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        resultBox.getItems().addAll("Pass", "Fail");
        resultBox.setValue("Pass");

        idCol.setCellValueFactory(new PropertyValueFactory<>("inspectionId"));
        batchCol.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));

        inspectionTable.setItems(rows);
        batchBox.setItems(batches);

        loadInspections();
    }

    @FXML
    public void loadInspections() {
        List<QualityInspection> stored = loadInspectionsFromBin("quality_inspections.bin");
        rows.setAll(stored);

        List<ProductionBatch> allBatches = loadBatchesFromBin("batches.bin");
        batches.setAll(allBatches);

        int pass = 0, fail = 0;
        for (int i = 0; i < stored.size(); i++) {
            String r = stored.get(i).getResult();
            if (r != null && r.equalsIgnoreCase("Pass")) pass++;
            else fail++;
        }
        totalLabel.setText(String.valueOf(stored.size()));
        passLabel.setText(String.valueOf(pass));
        failLabel.setText(String.valueOf(fail));

        infoLabel.setText("Loaded " + stored.size() + " inspection record(s).");
    }

    @FXML
    public void handleRecord() {
        ProductionBatch b = batchBox.getValue();
        String result = resultBox.getValue();
        if (b == null) {
            AlertHelper.warn("Validation", "Please select a batch.");
            return;
        }
        if (result == null) {
            AlertHelper.warn("Validation", "Please select a result.");
            return;
        }

        List<QualityInspection> list = loadInspectionsFromBin("quality_inspections.bin");
        String newId = "QI-" + String.format("%03d", list.size() + 1);
        QualityInspection qi = new QualityInspection(newId, b.getBatchId(), result);
        list.add(qi);
        saveInspectionsToBin("quality_inspections.bin", list);

        AlertHelper.info("Saved", "Inspection " + newId + " (" + result + ") recorded for " + b.getBatchId() + ".");
        loadInspections();
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

    private void saveInspectionsToBin(String fileName, List<QualityInspection> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (QualityInspection qi : list) oos.writeObject(qi);
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
