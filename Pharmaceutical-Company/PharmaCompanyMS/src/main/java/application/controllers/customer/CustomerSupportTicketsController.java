package application.controllers.customer;

import application.model.customer.SupportTicket;
import application.users.CurrentUser;
import application.utilities.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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

public class CustomerSupportTicketsController extends application.controllers.DashboardBaseController {

    @FXML private TextArea issueArea;
    @FXML private TableView<SupportTicket> ticketTable;
    @FXML private TableColumn<SupportTicket, String> idCol;
    @FXML private TableColumn<SupportTicket, String> custCol;
    @FXML private TableColumn<SupportTicket, String> issueCol;
    @FXML private Label infoLabel;

    private final ObservableList<SupportTicket> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        custCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        issueCol.setCellValueFactory(new PropertyValueFactory<>("issue"));
        ticketTable.setItems(rows);
        loadTickets();
    }

    @FXML
    public void handleSubmit() {
        String issue = issueArea.getText() == null ? "" : issueArea.getText().trim();
        if (issue == null || issue.trim().isEmpty()) {
            AlertHelper.warn("Validation", "Ticket description cannot be empty.");
            return;
        }

        List<SupportTicket> tickets = loadTicketsFromBin("support_tickets.bin");
        String newId = "T-" + (1000 + tickets.size() + 1);
        tickets.add(new SupportTicket(newId, CurrentUser.get().getUserId(), issue));
        saveTicketsToBin("support_tickets.bin", tickets);

        AlertHelper.info("Submitted", "Ticket " + newId + " submitted. Support will follow up.");
        infoLabel.setText("Last submitted: " + newId);
        issueArea.clear();
        loadTickets();
    }

    @FXML public void handleClear() { issueArea.clear(); }

    @FXML
    public void loadTickets() {
        String me = CurrentUser.get() == null ? "" : CurrentUser.get().getUserId();
        rows.clear();
        List<SupportTicket> all = loadTicketsFromBin("support_tickets.bin");
        int count = 0;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getCustomerId().equals(me)) {
                rows.add(all.get(i));
                count++;
            }
        }
        infoLabel.setText("Loaded " + count + " ticket(s) for your account.");
    }

    private List<SupportTicket> loadTicketsFromBin(String fileName) {
        List<SupportTicket> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((SupportTicket) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveTicketsToBin(String fileName, List<SupportTicket> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (SupportTicket t : list) oos.writeObject(t);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
