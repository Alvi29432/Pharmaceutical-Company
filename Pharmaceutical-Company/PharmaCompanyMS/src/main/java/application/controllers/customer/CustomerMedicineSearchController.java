package application.controllers.customer;

import application.model.common.Medicine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomerMedicineSearchController extends application.controllers.DashboardBaseController {

    @FXML private TextField  keywordField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TableView<Medicine> medicineTable;
    @FXML private TableColumn<Medicine, String> idCol;
    @FXML private TableColumn<Medicine, String> nameCol;
    @FXML private TableColumn<Medicine, String> categoryCol;
    @FXML private TableColumn<Medicine, Double> priceCol;
    @FXML private Label infoLabel;

    private final ObservableList<Medicine> rows = FXCollections.observableArrayList();
    private List<Medicine> all = new ArrayList<>();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        medicineTable.setItems(rows);

        loadCategories();
        handleShowAll();
    }

    private void loadCategories() {
        all = loadMedicinesFromBin("medicines.bin");
        categoryBox.getItems().clear();
        categoryBox.getItems().add("All");
        for (int i = 0; i < all.size(); i++) {
            String c = all.get(i).getCategory();
            if (c == null) continue;
            if (!categoryBox.getItems().contains(c)) categoryBox.getItems().add(c);
        }
        categoryBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleShowAll() {
        all = loadMedicinesFromBin("medicines.bin");
        rows.setAll(all);
        infoLabel.setText("Showing all " + all.size() + " medicine(s).");
    }

    @FXML
    public void handleSearch() {
        String keyword = keywordField.getText() == null ? "" : keywordField.getText().trim().toLowerCase();
        String category = categoryBox.getValue() == null ? "All" : categoryBox.getValue();

        rows.clear();
        int matches = 0;
        for (int i = 0; i < all.size(); i++) {
            Medicine m = all.get(i);
            boolean matchesCategory = category.equals("All") || category.equalsIgnoreCase(m.getCategory());
            boolean matchesKeyword = keyword.isEmpty()
                    || m.getId().toLowerCase().contains(keyword)
                    || m.getName().toLowerCase().contains(keyword);
            if (matchesCategory && matchesKeyword) {
                rows.add(m);
                matches++;
            }
        }
        infoLabel.setText("Found " + matches + " matching medicine(s).");
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
