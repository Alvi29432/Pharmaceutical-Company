package application.controllers.chiefexecutiveofficer;

import application.model.common.Feedback;
import application.model.common.Review;
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

public class CEOCustomerSatisfactionController extends application.controllers.DashboardBaseController {

    @FXML private Label avgRatingLabel;
    @FXML private Label reviewsLabel;
    @FXML private Label feedbackLabel;

    @FXML private TableView<AvgRow> avgTable;
    @FXML private TableColumn<AvgRow, String> avgMedCol;
    @FXML private TableColumn<AvgRow, String> avgValCol;

    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> fbIdCol;
    @FXML private TableColumn<Feedback, String> fbCustCol;
    @FXML private TableColumn<Feedback, String> fbMsgCol;

    @FXML private Label infoLabel;

    private final ObservableList<AvgRow> avgRows = FXCollections.observableArrayList();
    private final ObservableList<Feedback> feedbackRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        avgMedCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        avgValCol.setCellValueFactory(new PropertyValueFactory<>("average"));
        avgTable.setItems(avgRows);

        fbIdCol.setCellValueFactory(new PropertyValueFactory<>("feedbackId"));
        fbCustCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        fbMsgCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        feedbackTable.setItems(feedbackRows);

        loadAll();
    }

    @FXML
    public void loadAll() {
        List<Review> reviews = loadReviewsFromBin("reviews.bin");
        reviewsLabel.setText(String.valueOf(reviews.size()));
        if (reviews.isEmpty()) {
            avgRatingLabel.setText("0.00 / 5");
        } else {
            int total = 0;
            for (Review r : reviews) total += r.getRating();
            double avg = (double) total / reviews.size();
            avgRatingLabel.setText(String.format("%.2f / 5", avg));
        }

        avgRows.clear();
        for (int i = 0; i < reviews.size(); i++) {
            String medId = reviews.get(i).getMedicineId();
            boolean already = false;
            for (int j = 0; j < avgRows.size(); j++) {
                if (avgRows.get(j).getMedicineId().equals(medId)) { already = true; break; }
            }
            if (already) continue;

            int sum = 0;
            int count = 0;
            for (int k = 0; k < reviews.size(); k++) {
                if (reviews.get(k).getMedicineId().equals(medId)) {
                    sum += reviews.get(k).getRating();
                    count++;
                }
            }
            double medAvg = count == 0 ? 0.0 : (double) sum / count;
            avgRows.add(new AvgRow(medId, String.format("%.2f / 5 (%d reviews)", medAvg, count)));
        }

        feedbackRows.clear();
        feedbackRows.addAll(loadFeedbackFromBin("feedback.bin"));
        feedbackLabel.setText(String.valueOf(feedbackRows.size()));

        infoLabel.setText("Loaded " + reviews.size() + " review(s) and " + feedbackRows.size() + " feedback entry(ies).");
    }

    public static class AvgRow {
        private final String medicineId;
        private final String average;

        public AvgRow(String medicineId, String average) {
            this.medicineId = medicineId;
            this.average = average;
        }
        public String getMedicineId() { return medicineId; }
        public String getAverage() { return average; }
    }

    private List<Review> loadReviewsFromBin(String fileName) {
        List<Review> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Review) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
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
}
