package application.controllers.customer;

import application.model.common.Feedback;
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

public class CustomerFeedbackController extends application.controllers.DashboardBaseController {

    @FXML private TextArea messageArea;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> idCol;
    @FXML private TableColumn<Feedback, String> custCol;
    @FXML private TableColumn<Feedback, String> msgCol;
    @FXML private Label infoLabel;

    private final ObservableList<Feedback> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("feedbackId"));
        custCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        msgCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        feedbackTable.setItems(rows);
        loadFeedback();
    }

    @FXML
    public void handleSubmit() {
        String msg = messageArea.getText() == null ? "" : messageArea.getText().trim();
        if (msg.isEmpty()) {
            AlertHelper.warn("Validation", "Feedback message cannot be empty.");
            return;
        }

        List<Feedback> feedback = loadFeedbackFromBin("feedback.bin");
        String newId = "F-" + (1000 + feedback.size() + 1);
        feedback.add(new Feedback(newId, CurrentUser.get().getUserId(), msg));
        saveFeedbackToBin("feedback.bin", feedback);

        AlertHelper.info("Submitted", "Feedback " + newId + " submitted.");
        infoLabel.setText("Last submitted: " + newId);
        messageArea.clear();
        loadFeedback();
    }

    @FXML
    public void handleClear() { messageArea.clear(); }

    @FXML
    public void loadFeedback() {
        String me = CurrentUser.get() == null ? "" : CurrentUser.get().getUserId();
        rows.clear();
        List<Feedback> all = loadFeedbackFromBin("feedback.bin");
        int count = 0;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getCustomerId().equals(me)) {
                rows.add(all.get(i));
                count++;
            }
        }
        infoLabel.setText("Loaded " + count + " feedback entry(ies) for your account.");
    }

    private List<Feedback> loadFeedbackFromBin(String fileName) {
        List<Feedback> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Feedback) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveFeedbackToBin(String fileName, List<Feedback> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Feedback f : list) oos.writeObject(f);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
