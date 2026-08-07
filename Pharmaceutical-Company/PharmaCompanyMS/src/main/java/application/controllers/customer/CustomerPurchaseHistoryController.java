package application.controllers.customer;

import application.model.common.Medicine;
import application.model.customer.Order;
import application.users.CurrentUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerPurchaseHistoryController extends application.controllers.DashboardBaseController {

    @FXML private Label orderCountLabel;
    @FXML private Label totalSpentLabel;
    @FXML private TableView<CustomerTrackOrderController.OrderRow> historyTable;
    @FXML private TableColumn<CustomerTrackOrderController.OrderRow, String> orderIdCol;
    @FXML private TableColumn<CustomerTrackOrderController.OrderRow, String> medicineCol;
    @FXML private TableColumn<CustomerTrackOrderController.OrderRow, Integer> qtyCol;
    @FXML private TableColumn<CustomerTrackOrderController.OrderRow, String> statusCol;
    @FXML private TableColumn<CustomerTrackOrderController.OrderRow, Double> totalCol;
    @FXML private Label infoLabel;

    private final ObservableList<CustomerTrackOrderController.OrderRow> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        medicineCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        historyTable.setItems(rows);
        loadHistory();
    }

    @FXML
    public void loadHistory() {
        List<Order> orders = loadOrdersFromBin("orders.bin");
        List<Medicine> meds = loadMedicinesFromBin("medicines.bin");

        Map<String, String> statuses = new HashMap<>();
        List<String[]> stored = loadOrderStatusFromBin("order_status.bin");
        for (int i = 0; i < stored.size(); i++) statuses.put(stored.get(i)[0], stored.get(i)[1]);

        String me = CurrentUser.get() == null ? "" : CurrentUser.get().getUserId();
        rows.clear();
        double totalSpent = 0.0;
        int count = 0;
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            if (!o.getCustomerId().equals(me)) continue;
            double unit = 0.0;
            for (int j = 0; j < meds.size(); j++) {
                if (meds.get(j).getId().equals(o.getMedicineId())) { unit = meds.get(j).getPrice(); break; }
            }
            double total = unit * o.getQuantity();
            totalSpent += total;
            count++;
            rows.add(new CustomerTrackOrderController.OrderRow(
                    o.getOrderId(), o.getMedicineId(), o.getQuantity(),
                    statuses.getOrDefault(o.getOrderId(), "Placed"), total));
        }
        orderCountLabel.setText(String.valueOf(count));
        totalSpentLabel.setText(String.format("%.2f", totalSpent));
        infoLabel.setText("Loaded " + count + " historical order(s).");
    }

    private List<Order> loadOrdersFromBin(String fileName) {
        List<Order> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Order) ois.readObject()); }
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

    private List<String[]> loadOrderStatusFromBin(String fileName) {
        List<String[]> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((String[]) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }
}
