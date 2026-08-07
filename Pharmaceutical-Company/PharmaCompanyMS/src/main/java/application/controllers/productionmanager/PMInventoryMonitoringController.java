package application.controllers.productionmanager;

import application.model.common.InventoryItem;
import application.model.common.Medicine;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class PMInventoryMonitoringController extends application.controllers.DashboardBaseController {

    @FXML private Label itemCountLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label outOfStockLabel;
    @FXML private Label infoLabel;

    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, String>  idCol;
    @FXML private TableColumn<InventoryItem, String>  medicineCol;
    @FXML private TableColumn<InventoryItem, Integer> stockCol;
    @FXML private TableColumn<InventoryItem, String>  statusCol;

    private final ObservableList<InventoryItem> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        medicineCol.setCellValueFactory(c -> {
            String mid = c.getValue().getMedicineId();
            List<Medicine> meds = loadMedicinesFromBin("medicines.bin");
            String name = mid;
            for (int i = 0; i < meds.size(); i++) {
                if (meds.get(i).getId().equals(mid)) {
                    name = meds.get(i).getName();
                    break;
                }
            }
            return new SimpleStringProperty(name);
        });
        statusCol.setCellValueFactory(c -> {
            int s = c.getValue().getStock();
            String label;
            if (s <= 0) label = "Out of stock";
            else if (s < 100) label = "Low";
            else label = "OK";
            return new SimpleStringProperty(label);
        });

        inventoryTable.setItems(rows);
        loadInventory();
    }

    @FXML
    public void loadInventory() {
        List<InventoryItem> stored = loadInventoryFromBin("inventory.bin");
        rows.setAll(stored);

        int low = 0;
        int out = 0;
        for (int i = 0; i < stored.size(); i++) {
            int s = stored.get(i).getStock();
            if (s <= 0) out++;
            else if (s < 100) low++;
        }
        itemCountLabel.setText(String.valueOf(stored.size()));
        lowStockLabel.setText(String.valueOf(low));
        outOfStockLabel.setText(String.valueOf(out));

        infoLabel.setText("Loaded " + stored.size() + " inventory record(s).");
    }

    private List<InventoryItem> loadInventoryFromBin(String fileName) {
        List<InventoryItem> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((InventoryItem) ois.readObject()); }
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
