package application.controllers.productionmanager;

import application.model.productionmanager.StaffShift;
import application.utilities.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

public class PMStaffShiftController extends application.controllers.DashboardBaseController {

    @FXML private TextField employeeField;
    @FXML private TextField dateField;
    @FXML private ComboBox<String> shiftBox;
    @FXML private Label infoLabel;

    @FXML private TableView<StaffShift> shiftTable;
    @FXML private TableColumn<StaffShift, String> idCol;
    @FXML private TableColumn<StaffShift, String> empCol;
    @FXML private TableColumn<StaffShift, String> dateCol;
    @FXML private TableColumn<StaffShift, String> typeCol;

    private final ObservableList<StaffShift> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        shiftBox.getItems().addAll(StaffShift.SHIFT_MORNING, StaffShift.SHIFT_EVENING, StaffShift.SHIFT_NIGHT);
        shiftBox.setValue(StaffShift.SHIFT_MORNING);

        idCol.setCellValueFactory(new PropertyValueFactory<>("shiftId"));
        empCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("shiftType"));

        shiftTable.setItems(rows);
        loadShifts();
    }

    @FXML
    public void loadShifts() {
        List<StaffShift> stored = loadShiftsFromBin("staff_shifts.bin");
        rows.setAll(stored);
        infoLabel.setText("Loaded " + stored.size() + " shift record(s).");
    }

    @FXML
    public void handleAssign() {
        String employee = employeeField.getText() == null ? "" : employeeField.getText().trim();
        String date = dateField.getText() == null ? "" : dateField.getText().trim();
        String type = shiftBox.getValue();

        if (employee == null || employee.trim().isEmpty() || date == null || date.trim().isEmpty() || type == null) {
            AlertHelper.warn("Validation", "Please fill employee, date and shift.");
            return;
        }

        List<StaffShift> list = loadShiftsFromBin("staff_shifts.bin");
        String newId = "SS-" + String.format("%03d", list.size() + 1);
        StaffShift s = new StaffShift(newId, employee, date, type);
        list.add(s);
        saveShiftsToBin("staff_shifts.bin", list);

        AlertHelper.info("Saved", "Shift " + newId + " assigned to " + employee + ".");
        employeeField.clear();
        dateField.clear();
        loadShifts();
    }

    private List<StaffShift> loadShiftsFromBin(String fileName) {
        List<StaffShift> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((StaffShift) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveShiftsToBin(String fileName, List<StaffShift> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (StaffShift s : list) oos.writeObject(s);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
