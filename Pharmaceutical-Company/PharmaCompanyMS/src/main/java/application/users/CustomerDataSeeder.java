package application.users;

import application.model.customer.Invoice;
import application.model.common.Medicine;
import application.model.common.Notification;
import application.model.customer.Order;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public final class CustomerDataSeeder {

    private CustomerDataSeeder() {}

    public static void seedIfEmpty() {
        seedOrders();
        seedInvoices();
        seedNotifications();
    }

    private static void seedOrders() {
        if (!loadOrdersFromBin("orders.bin").isEmpty()) return;
        List<Order> list = new ArrayList<>();
        // status is tracked separately in order_status.bin so the Order model
        // stays unchanged from earlier phases.
        list.add(new Order("O-1001", "U-002", "M-001", 2));
        list.add(new Order("O-1002", "U-002", "M-004", 1));
        saveOrdersToBin("orders.bin", list);

        List<String[]> statuses = new ArrayList<>();
        statuses.add(new String[] { "O-1001", "Delivered" });
        statuses.add(new String[] { "O-1002", "Shipped" });
        saveOrderStatusToBin("order_status.bin", statuses);
    }

    private static void seedInvoices() {
        if (!loadInvoicesFromBin("invoices.bin").isEmpty()) return;
        List<Medicine> meds = loadMedicinesFromBin("medicines.bin");
        double priceOf = 0.0;
        String medId = "M-001";
        // look up the unit price for M-001 with a for loop
        for (int i = 0; i < meds.size(); i++) {
            Medicine m = meds.get(i);
            if (m.getId().equals(medId)) { priceOf = m.getPrice(); break; }
        }
        List<Invoice> list = new ArrayList<>();
        list.add(new Invoice("INV-1001", "O-1001", priceOf * 2));
        list.add(new Invoice("INV-1002", "O-1002", 0.80));
        saveInvoicesToBin("invoices.bin", list);
    }

    private static void seedNotifications() {
        if (!loadNotificationsFromBin("notifications.bin").isEmpty()) return;
        List<Notification> list = new ArrayList<>();
        list.add(new Notification("N-001", "U-002", "Welcome to Pharma Company MS!"));
        list.add(new Notification("N-002", "U-002", "Your order O-1001 has been delivered."));
        list.add(new Notification("N-003", "All",    "Scheduled maintenance on Sunday 02:00-04:00."));
        saveNotificationsToBin("notifications.bin", list);
    }

    private static <T> List<T> loadList(String filename, Class<T> type) {
        List<T> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (type.isInstance(obj)) out.add(type.cast(obj));
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private static <T> void saveList(String filename, List<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            for (T item : list) oos.writeObject(item);
        } catch (IOException ignored) {}
    }

    private static List<Order> loadOrdersFromBin(String fn) { return loadList(fn, Order.class); }
    private static void saveOrdersToBin(String fn, List<Order> list) { saveList(fn, list); }
    private static List<String[]> loadOrderStatusFromBin(String fn) { return loadList(fn, String[].class); }
    private static void saveOrderStatusToBin(String fn, List<String[]> list) { saveList(fn, list); }
    private static List<Invoice> loadInvoicesFromBin(String fn) { return loadList(fn, Invoice.class); }
    private static void saveInvoicesToBin(String fn, List<Invoice> list) { saveList(fn, list); }
    private static List<Medicine> loadMedicinesFromBin(String fn) { return loadList(fn, Medicine.class); }
    private static List<Notification> loadNotificationsFromBin(String fn) { return loadList(fn, Notification.class); }
    private static void saveNotificationsToBin(String fn, List<Notification> list) { saveList(fn, list); }
}
