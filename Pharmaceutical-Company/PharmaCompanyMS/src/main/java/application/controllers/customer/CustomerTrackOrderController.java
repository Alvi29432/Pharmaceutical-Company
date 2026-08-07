package application.controllers.customer;

import application.model.common.Medicine;
import application.model.common.Notification;
import application.model.customer.Order;
import application.users.CurrentUser;
import application.utilities.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerTrackOrderController extends application.controllers.DashboardBaseController {

    @FXML private ComboBox<String> statusFilterBox;
    @FXML private TableView<OrderRow> orderTable;
    @FXML private TableColumn<OrderRow, String> orderIdCol;
    @FXML private TableColumn<OrderRow, String> medicineCol;
    @FXML private TableColumn<OrderRow, Integer> qtyCol;
    @FXML private TableColumn<OrderRow, String> statusCol;
    @FXML private TableColumn<OrderRow, Double> totalCol;
    @FXML private Label infoLabel;

    private final ObservableList<OrderRow> rows = FXCollections.observableArrayList();
    private final List<Order> allOrders = new ArrayList<>();
    private final List<Medicine> allMedicines = new ArrayList<>();
    private Map<String, String> statusMap = new HashMap<>();

    @FXML
    public void initialize() {
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        medicineCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        orderTable.setItems(rows);

        statusFilterBox.getItems().setAll("All", "Placed", "Shipped", "Delivered");
        statusFilterBox.getSelectionModel().selectFirst();

        loadOrders();
    }

    @FXML
    public void loadOrders() {
        allOrders.clear();    allOrders.addAll(loadOrdersFromBin("orders.bin"));
        allMedicines.clear(); allMedicines.addAll(loadMedicinesFromBin("medicines.bin"));

        statusMap.clear();
        List<String[]> stored = loadOrderStatusFromBin("order_status.bin");
        for (int i = 0; i < stored.size(); i++) {
            statusMap.put(stored.get(i)[0], stored.get(i)[1]);
        }

        applyFilter(statusFilterBox.getValue());
    }

    @FXML
    public void handleApplyFilter() {
        applyFilter(statusFilterBox.getValue());
    }

    private void applyFilter(String filter) {
        rows.clear();
        String me = CurrentUser.get() == null ? "" : CurrentUser.get().getUserId();
        int count = 0;
        for (int i = 0; i < allOrders.size(); i++) {
            Order o = allOrders.get(i);
            if (!o.getCustomerId().equals(me)) continue;
            String st = statusMap.getOrDefault(o.getOrderId(), "Placed");
            if (filter != null && !filter.equals("All") && !filter.equals(st)) continue;

            double unit = 0.0;
            for (int j = 0; j < allMedicines.size(); j++) {
                if (allMedicines.get(j).getId().equals(o.getMedicineId())) {
                    unit = allMedicines.get(j).getPrice();
                    break;
                }
            }
            rows.add(new OrderRow(o.getOrderId(), o.getMedicineId(), o.getQuantity(), st, unit * o.getQuantity()));
            count++;
        }
        infoLabel.setText("Showing " + count + " order(s).");
    }

    private void setStatus(String newStatus) {
        OrderRow sel = orderTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Selection", "Please choose an order from the table first.");
            return;
        }
        List<String[]> stored = loadOrderStatusFromBin("order_status.bin");
        boolean found = false;
        for (int i = 0; i < stored.size(); i++) {
            if (stored.get(i)[0].equals(sel.getOrderId())) {
                stored.set(i, new String[] { sel.getOrderId(), newStatus });
                found = true;
                break;
            }
        }
        if (!found) stored.add(new String[] { sel.getOrderId(), newStatus });
        saveOrderStatusToBin("order_status.bin", stored);

        List<Notification> notifications = loadNotificationsFromBin("notifications.bin");
        String custId = CurrentUser.get() == null ? "" : CurrentUser.get().getUserId();
        notifications.add(new Notification(
                "N-" + (1000 + notifications.size() + 1),
                custId,
                "Order " + sel.getOrderId() + " is now " + newStatus + "."));
        saveNotificationsToBin("notifications.bin", notifications);

        AlertHelper.info("Updated", "Order " + sel.getOrderId() + " is now " + newStatus + ".");
        loadOrders();
    }

    @FXML public void handleAdvanceShipped()   { setStatus("Shipped"); }
    @FXML public void handleAdvanceDelivered() { setStatus("Delivered"); }

    public static class OrderRow {
        private final String orderId;
        private final String medicineId;
        private final int quantity;
        private final String status;
        private final double total;
        public OrderRow(String orderId, String medicineId, int quantity, String status, double total) {
            this.orderId = orderId; this.medicineId = medicineId;
            this.quantity = quantity; this.status = status; this.total = total;
        }
        public String getOrderId() { return orderId; }
        public String getMedicineId() { return medicineId; }
        public int getQuantity() { return quantity; }
        public String getStatus() { return status; }
        public double getTotal() { return total; }
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

    private void saveOrderStatusToBin(String fileName, List<String[]> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (String[] s : list) oos.writeObject(s);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private List<Notification> loadNotificationsFromBin(String fileName) {
        List<Notification> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Notification) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveNotificationsToBin(String fileName, List<Notification> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Notification n : list) oos.writeObject(n);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
