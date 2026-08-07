package application.controllers.productionmanager;

import application.model.common.Incident;
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

public class PMIncidentManagementController extends application.controllers.DashboardBaseController {

    @FXML private Label totalLabel;
    @FXML private Label infoLabel;

    @FXML private TextArea descArea;

    @FXML private TableView<Incident> incidentTable;
    @FXML private TableColumn<Incident, String> idCol;
    @FXML private TableColumn<Incident, String> descCol;

    private final ObservableList<Incident> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("incidentId"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        incidentTable.setItems(rows);
        loadIncidents();
    }

    @FXML
    public void loadIncidents() {
        rows.clear();
        List<Incident> ceo = loadIncidentsFromBin("incidents.bin");
        List<Incident> pm  = loadIncidentsFromBin("incidents_pm.bin");

        for (int i = 0; i < ceo.size(); i++) rows.add(ceo.get(i));
        for (int i = 0; i < pm.size(); i++)  rows.add(pm.get(i));

        totalLabel.setText(String.valueOf(rows.size()));
        infoLabel.setText("Loaded " + ceo.size() + " CEO + " + pm.size() + " PM incident(s).");
    }

    @FXML
    public void handleLog() {
        String desc = descArea.getText() == null ? "" : descArea.getText().trim();
        if (desc == null || desc.trim().isEmpty()) {
            AlertHelper.warn("Validation", "Please describe the incident.");
            return;
        }

        List<Incident> list = loadIncidentsFromBin("incidents_pm.bin");
        String newId = "PM-IN-" + String.format("%03d", list.size() + 1);
        Incident inc = new Incident(newId, desc);
        list.add(inc);
        saveIncidentsToBin("incidents_pm.bin", list);

        AlertHelper.info("Saved", "Incident " + newId + " logged.");
        descArea.clear();
        loadIncidents();
    }

    @FXML
    public void handleClear() {
        descArea.clear();
    }

    private List<Incident> loadIncidentsFromBin(String fileName) {
        List<Incident> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Incident) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void saveIncidentsToBin(String fileName, List<Incident> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (Incident i : list) oos.writeObject(i);
            oos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
