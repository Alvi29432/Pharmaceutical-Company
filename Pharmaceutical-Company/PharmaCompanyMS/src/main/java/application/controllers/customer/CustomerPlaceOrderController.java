package application.controllers.customer;

import application.model.customer.Invoice;
import application.model.common.InventoryItem;
import application.model.common.Medicine;
import application.model.common.Notification;
import application.model.customer.Order;
import application.users.CurrentUser;
import application.utilities.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomerPlaceOrderController extends application.controllers.DashboardBaseController {

    @FXML private ComboBox<Medicine> medicineBox;
    @FXML private Label stockLabel;
    @FXML private Label priceLabel;
    @FXML private Label totalLabel;
    @FXML private Spinner<Integer> qtySpinner;
    @FXML private Label infoLabel;

    private final List<Medicine> medicines = new ArrayList<>();
    private final List<InventoryItem> inventory = new ArrayList<>();

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1);
        qtySpinner.setValueFactory(factory);
        qtySpinner.valueProperty().addListener((obs, o, n) -> handleRecalculate());

        medicineBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            refreshDerivedFields();
        });

        loadCatalog();
    }

    private void loadCatalog() {
        medicines.clear();
        List<Medicine> medList = loadMedicinesFromBin("medicines.bin");
        for (int i = 0; i < medList.size(); i++) medicines.add(medList.get(i));
        medicineBox.setItems(FXCollections.observableArrayList(medicines));
        if (!medicines.isEmpty()) medicineBox.getSelectionModel().selectFirst();
        inventory.clear();
        List<InventoryItem> invList = loadInventoryFromBin("inventory.bin");
        for (int i = 0; i < invList.size(); i++) inventory.add(invList.get(i));
        refreshDerivedFields();
    }

    private void refreshDerivedFields() {
        Medicine m = medicineBox.getValue();
        if (m == null) {
            stockLabel.setText("-");
            priceLabel.setText("-");
            totalLabel.setText("-");
            return;
        }
        int stock = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getMedicineId().equals(m.getId())) {
                stock = inventory.get(i).getStock();
                break;
            }
        }
        stockLabel.setText(String.valueOf(stock));
        priceLabel.setText(String.format("%.2f", m.getPrice()));
        handleRecalculate();
    }

    @FXML
    public void handleRecalculate() {
        Medicine m = medicineBox.getValue();
        if (m == null) { totalLabel.setText("-"); return; }
        int q = qtySpinner.getValue() == null ? 0 : qtySpinner.getValue();
        totalLabel.setText(String.format("%.2f", m.getPrice() * q));
    }

    @FXML
    public void handlePlaceOrder() {
        String userId = CurrentUser.get() == null ? null : CurrentUser.get().getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            AlertHelper.error("Error", "You must be logged in to place an order.");
            return;
        }
        Medicine med = medicineBox.getValue();
        if (med == null) {
            AlertHelper.warn("Validation", "Please choose a medicine.");
            return;
        }
        int qty = qtySpinner.getValue() == null ? 0 : qtySpinner.getValue();
        if (qty <= 0) {
            AlertHelper.warn("Validation", "Quantity must be at least 1.");
            return;
        }

        int stock = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getMedicineId().equals(med.getId())) {
                stock = inventory.get(i).getStock();
                break;
            }
        }
        if (qty > stock) {
            AlertHelper.warn("Stock", "Only " + stock + " unit(s) available.");
            return;
        }

        List<Order> orders = loadOrdersFromBin("orders.bin");
        String orderId = "O-" + (1000 + orders.size() + 1);
        Order newOrder = new Order(orderId, userId, med.getId(), qty);
        orders.add(newOrder);
        saveOrdersToBin("orders.bin", orders);

        List<String[]> statuses = loadOrderStatusFromBin("order_status.bin");
        statuses.add(new String[] { orderId, "Placed" });
        saveOrderStatusToBin("order_status.bin", statuses);

        List<Invoice> invoices = loadInvoicesFromBin("invoices.bin");
        String invoiceId = "INV-" + (1000 + invoices.size() + 1);
        double amount = med.getPrice() * qty;
        invoices.add(new Invoice(invoiceId, orderId, amount));
        saveInvoicesToBin("invoices.bin", invoices);

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getMedicineId().equals(med.getId())) {
                InventoryItem it = inventory.get(i);
                it.setStock(it.getStock() - qty);
                inventory.set(i, it);
                break;
            }
        }
        saveInventoryToBin("inventory.bin", inventory);

        List<Notification> notifications = loadNotificationsFromBin("notifications.bin");
        notifications.add(new Notification(
                "N-" + (1000 + notifications.size() + 1),
                userId,
                "Order " + orderId + " placed. Invoice " + invoiceId + " issued for $" + String.format("%.2f", amount) + "."));
        saveNotificationsToBin("notifications.bin", notifications);

        AlertHelper.info("Order Placed",
                "Order " + orderId + " placed for " + qty + " x " + med.getName()
                        + ".\nInvoice " + invoiceId + " issued for $" + String.format("%.2f", amount) + ".");

infoLabel.setText("Last order: " + orderId + " - invoice " + invoiceId);
        loadCatalog();    }

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

    private void saveInventoryToBin(String fileName, List<InventoryItem> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (InventoryItem it : list) oos.writeObject(it);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
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

    private void saveOrdersToBin(String fileName, List<Order> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Order o : list) oos.writeObject(o);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private List<Invoice> loadInvoicesFromBin(String fileName) {
        List<Invoice> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Invoice) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveInvoicesToBin(String fileName, List<Invoice> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Invoice inv : list) oos.writeObject(inv);
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
}
