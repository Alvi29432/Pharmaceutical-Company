package application.controllers.chiefexecutiveofficer;

import application.model.common.InventoryItem;
import application.utilities.AlertHelper;
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

public class CEOInventoryController extends application.controllers.DashboardBaseController {

    @FXML private ComboBox<String> filterBox;
    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, String> idCol;
    @FXML private TableColumn<InventoryItem, String> medCol;
    @FXML private TableColumn<InventoryItem, Integer> stockCol;
    @FXML private TableColumn<InventoryItem, String> statusCol;
    @FXML private TextField thresholdField;
    @FXML private Label infoLabel;

    private static final String FILE = "inventory.bin";
    private static final int DEFAULT_THRESHOLD = 50;
    private int threshold = DEFAULT_THRESHOLD;
    private final ObservableList<InventoryItem> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        medCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));

        statusCol.setCellValueFactory(cell -> {
            int stock = cell.getValue().getStock();
            String text;
            if (stock <= 0)       text = "Out of Stock";
            else if (stock <= threshold) text = "Low Stock";
            else                  text = "OK";
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        inventoryTable.setItems(rows);

        filterBox.setItems(FXCollections.observableArrayList("All", "Low Stock", "Out of Stock", "OK"));
        filterBox.setValue("All");
        filterBox.setOnAction(e -> loadInventory());

        loadInventory();
    }

    @FXML
    public void loadInventory() {
        List<InventoryItem> all = loadInventoryFromBin(FILE);
        rows.clear();
        String filter = filterBox.getValue();
        for (InventoryItem item : all) {
            String status = classify(item.getStock());
            if ("All".equals(filter) || filter.equals(status)) {
                rows.add(item);
            }
        }
        infoLabel.setText("Showing " + rows.size() + " item(s). Threshold: " + threshold + ".");
    }

    @FXML
    private void handleUpdateThreshold() {
        String text = thresholdField.getText();
        if (text == null || text.trim().isEmpty()) {
            AlertHelper.warn("Invalid threshold", "Please enter a positive number.");
            return;
        }
        try {
            int parsed = Integer.parseInt(text.trim());
            if (parsed < 0) throw new NumberFormatException();
            threshold = parsed;
            loadInventory();
        } catch (NumberFormatException ex) {
            AlertHelper.error("Invalid threshold", "Threshold must be a non-negative whole number.");
        }
    }

    private String classify(int stock) {
        if (stock <= 0) return "Out of Stock";
        if (stock <= threshold) return "Low Stock";
        return "OK";
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
}
