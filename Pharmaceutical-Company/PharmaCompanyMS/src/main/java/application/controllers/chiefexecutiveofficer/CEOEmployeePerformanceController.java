package application.controllers.chiefexecutiveofficer;

import application.model.chiefexecutiveofficer.EmployeePerformance;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class CEOEmployeePerformanceController extends application.controllers.DashboardBaseController {

    @FXML private Label avgScoreLabel;
    @FXML private Label topEmployeeLabel;
    @FXML private ComboBox<String> sortBox;
    @FXML private TableView<EmployeePerformance> performanceTable;
    @FXML private TableColumn<EmployeePerformance, String> empCol;
    @FXML private TableColumn<EmployeePerformance, String> periodCol;
    @FXML private TableColumn<EmployeePerformance, Double> scoreCol;
    @FXML private Label infoLabel;

    private final ObservableList<EmployeePerformance> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        empCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        periodCol.setCellValueFactory(new PropertyValueFactory<>("period"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        performanceTable.setItems(rows);

        sortBox.setItems(FXCollections.observableArrayList("Score (High to Low)", "Score (Low to High)", "Employee ID"));
        sortBox.setValue("Score (High to Low)");
        sortBox.setOnAction(e -> loadAll());

        loadAll();
    }

    @FXML
    public void loadAll() {
        List<EmployeePerformance> all = loadPerformanceFromBin("performance.bin");
        rows.clear();
        rows.addAll(all);

        sortRows();

        if (rows.isEmpty()) {
            avgScoreLabel.setText("0.00");
            topEmployeeLabel.setText("-");
        } else {
            double sum = 0.0;
            for (int i = 0; i < rows.size(); i++) sum += rows.get(i).getScore();
            avgScoreLabel.setText(String.format("%.2f", sum / rows.size()));
            topEmployeeLabel.setText(rows.get(0).getEmployeeId() + " (" + rows.get(0).getPeriod() + ")");
        }
        infoLabel.setText("Showing " + rows.size() + " record(s).");
    }

    private void sortRows() {
        String mode = sortBox.getValue();
        if (mode == null) mode = "Score (High to Low)";

        for (int i = 1; i < rows.size(); i++) {
            EmployeePerformance current = rows.get(i);
            int j = i - 1;
            if (mode.equals("Score (Low to High)")) {
                for (; j >= 0; j--) {
                    if (rows.get(j).getScore() <= current.getScore()) break;
                    rows.set(j + 1, rows.get(j));
                }
            } else if (mode.equals("Employee ID")) {
                for (; j >= 0; j--) {
                    if (rows.get(j).getEmployeeId().compareTo(current.getEmployeeId()) <= 0) break;
                    rows.set(j + 1, rows.get(j));
                }
            } else {                for (; j >= 0; j--) {
                    if (rows.get(j).getScore() >= current.getScore()) break;
                    rows.set(j + 1, rows.get(j));
                }
            }
            rows.set(j + 1, current);
        }
    }

    private List<EmployeePerformance> loadPerformanceFromBin(String fileName) {
        List<EmployeePerformance> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((EmployeePerformance) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }
}
