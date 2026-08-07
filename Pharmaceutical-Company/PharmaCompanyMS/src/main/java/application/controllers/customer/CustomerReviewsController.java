package application.controllers.customer;

import application.model.common.Medicine;
import application.model.common.Review;
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
import java.util.List;

public class CustomerReviewsController extends application.controllers.DashboardBaseController {

    @FXML private ComboBox<Medicine> medicineBox;
    @FXML private ComboBox<Integer>  ratingBox;
    @FXML private TableView<ReviewRow> reviewTable;
    @FXML private TableColumn<ReviewRow, String> idCol;
    @FXML private TableColumn<ReviewRow, String> medicineCol;
    @FXML private TableColumn<ReviewRow, Integer> ratingCol;
    @FXML private Label infoLabel;

    private final ObservableList<ReviewRow> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ratingBox.getItems().setAll(1, 2, 3, 4, 5);
        ratingBox.getSelectionModel().selectFirst();

        medicineBox.setItems(FXCollections.observableArrayList(loadMedicinesFromBin("medicines.bin")));
        if (!medicineBox.getItems().isEmpty()) medicineBox.getSelectionModel().selectFirst();

        idCol.setCellValueFactory(new PropertyValueFactory<>("reviewId"));
        medicineCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        reviewTable.setItems(rows);

        medicineBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> loadReviews());
        loadReviews();
    }

    @FXML
    public void handleSubmit() {
        Medicine med = medicineBox.getValue();
        Integer rating = ratingBox.getValue();
        if (med == null) {
            AlertHelper.warn("Validation", "Please choose a medicine.");
            return;
        }
        if (rating == null || rating < 1 || rating > 5) {
            AlertHelper.warn("Validation", "Rating must be between 1 and 5.");
            return;
        }

        List<Review> reviews = loadReviewsFromBin("reviews.bin");
        String newId = "R-" + (1000 + reviews.size() + 1);
        reviews.add(new Review(newId, med.getId(), rating));
        saveReviewsToBin("reviews.bin", reviews);

        AlertHelper.info("Submitted", "Review " + newId + " submitted for " + med.getName() + ".");
        infoLabel.setText("Last submitted: " + newId);
        loadReviews();
    }

    @FXML
    public void loadReviews() {
        Medicine selected = medicineBox.getValue();
        List<Review> reviews = loadReviewsFromBin("reviews.bin");
        List<Medicine> meds = loadMedicinesFromBin("medicines.bin");

        rows.clear();
        int count = 0;
        for (int i = 0; i < reviews.size(); i++) {
            Review r = reviews.get(i);
            if (selected != null && !r.getMedicineId().equals(selected.getId())) continue;
            String name = r.getMedicineId();
            for (int j = 0; j < meds.size(); j++) {
                if (meds.get(j).getId().equals(r.getMedicineId())) { name = meds.get(j).getName(); break; }
            }
            rows.add(new ReviewRow(r.getReviewId(), name, r.getRating()));
            count++;
        }
        infoLabel.setText("Loaded " + count + " review(s)" + (selected == null ? "." : " for " + selected.getName() + "."));
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

    private void saveReviewsToBin(String fileName, List<Review> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Review r : list) oos.writeObject(r);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static class ReviewRow {
        private final String reviewId;
        private final String medicineName;
        private final int rating;
        public ReviewRow(String reviewId, String medicineName, int rating) {
            this.reviewId = reviewId; this.medicineName = medicineName; this.rating = rating;
        }
        public String getReviewId() { return reviewId; }
        public String getMedicineName() { return medicineName; }
        public int getRating() { return rating; }
    }
}
