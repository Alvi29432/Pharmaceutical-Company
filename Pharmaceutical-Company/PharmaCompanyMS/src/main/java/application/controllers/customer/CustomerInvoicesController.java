package application.controllers.customer;

import application.model.customer.Invoice;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerInvoicesController extends application.controllers.DashboardBaseController {

    @FXML private Label invoiceCountLabel;
    @FXML private Label totalBilledLabel;
    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> invoiceIdCol;
    @FXML private TableColumn<Invoice, String> orderIdCol;
    @FXML private TableColumn<Invoice, Double> amountCol;
    @FXML private Label infoLabel;

    private final ObservableList<Invoice> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        invoiceIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        invoiceTable.setItems(rows);
        loadInvoices();
    }

    @FXML
    public void loadInvoices() {
        List<Invoice> invoices = loadInvoicesFromBin("invoices.bin");
        List<Order> orders = loadOrdersFromBin("orders.bin");

        Set<String> myOrderIds = new HashSet<>();
        String me = CurrentUser.get() == null ? "" : CurrentUser.get().getUserId();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getCustomerId().equals(me)) {
                myOrderIds.add(orders.get(i).getOrderId());
            }
        }

        rows.clear();
        double total = 0.0;
        int count = 0;
        for (int i = 0; i < invoices.size(); i++) {
            Invoice inv = invoices.get(i);
            if (myOrderIds.contains(inv.getOrderId())) {
                rows.add(inv);
                total += inv.getAmount();
                count++;
            }
        }
        invoiceCountLabel.setText(String.valueOf(count));
        totalBilledLabel.setText(String.format("%.2f", total));
        infoLabel.setText("Loaded " + count + " invoice(s) for your account.");
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
}
