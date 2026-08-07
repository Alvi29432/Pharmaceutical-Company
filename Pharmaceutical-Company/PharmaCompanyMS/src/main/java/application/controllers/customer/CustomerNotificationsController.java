package application.controllers.customer;

import application.model.common.Notification;
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
import java.util.List;

public class CustomerNotificationsController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private TableView<Notification> notificationTable;
    @FXML private TableColumn<Notification, String> idCol;
    @FXML private TableColumn<Notification, String> recipientCol;
    @FXML private TableColumn<Notification, String> messageCol;
    @FXML private Label infoLabel;

    private final ObservableList<Notification> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("notificationId"));
        recipientCol.setCellValueFactory(new PropertyValueFactory<>("recipient"));
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        notificationTable.setItems(rows);
        loadNotifications();
    }

    @FXML
    public void loadNotifications() {
        String me = CurrentUser.get() == null ? "" : CurrentUser.get().getUserId();
        rows.clear();
        List<Notification> all = loadNotificationsFromBin("notifications.bin");
        int count = 0;
        for (int i = 0; i < all.size(); i++) {
            Notification n = all.get(i);
            if ("All".equalsIgnoreCase(n.getRecipient()) || n.getRecipient().equals(me)) {
                rows.add(n);
                count++;
            }
        }
        totalLabel.setText(String.valueOf(count));
        infoLabel.setText("Loaded " + count + " notification(s).");
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
}
