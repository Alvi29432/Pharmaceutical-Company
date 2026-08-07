package application.controllers.accountant;

import application.model.common.FinancialRecord;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class AccountantCashFlowController extends application.controllers.DashboardBaseController {

    @FXML private Label inflowLabel;
    @FXML private Label outflowLabel;
    @FXML private Label netLabel;
    @FXML private Label infoLabel;

    @FXML private TableView<CashFlowRow> cashTable;
    @FXML private TableColumn<CashFlowRow, String>  dateCol;
    @FXML private TableColumn<CashFlowRow, Double>  inCol;
    @FXML private TableColumn<CashFlowRow, Double>  outCol;
    @FXML private TableColumn<CashFlowRow, Double>  netCol;

    private final ObservableList<CashFlowRow> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date));
        inCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().inflow).asObject());
        outCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().outflow).asObject());
        netCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().net).asObject());
        cashTable.setItems(rows);
        loadCashFlow();
    }

    @FXML
    public void loadCashFlow() {
        rows.clear();
        List<FinancialRecord> all = loadFinancialRecordsFromBin("financial_records.bin");

        double totalIn = 0;
        double totalOut = 0;
        Map<String, double[]> byDate = new LinkedHashMap<>();
        int idx = 1;
        for (int i = 0; i < all.size(); i++) {
            FinancialRecord r = all.get(i);
            if (r.getType() == null) continue;
            String key = "Entry-" + String.format("%03d", idx++);
            double[] arr = byDate.computeIfAbsent(key, k -> new double[2]);
            if (r.getType().startsWith("REV-")) {
                arr[0] += r.getAmount();
                totalIn += r.getAmount();
            } else if (r.getType().startsWith("EXP-")) {
                arr[1] += r.getAmount();
                totalOut += r.getAmount();
            }
        }
        for (Map.Entry<String, double[]> e : byDate.entrySet()) {
            if (e.getValue()[0] == 0 && e.getValue()[1] == 0) continue;
            rows.add(new CashFlowRow(e.getKey(), e.getValue()[0], e.getValue()[1]));
        }

        inflowLabel.setText(String.format("%.2f", totalIn));
        outflowLabel.setText(String.format("%.2f", totalOut));
        netLabel.setText(String.format("%.2f", totalIn - totalOut));
        infoLabel.setText("Loaded " + rows.size() + " cash flow row(s).");
    }

    public static class CashFlowRow {
        public final String date;
        public final double inflow;
        public final double outflow;
        public final double net;
        public CashFlowRow(String date, double inflow, double outflow) {
            this.date = date;
            this.inflow = inflow;
            this.outflow = outflow;
            this.net = inflow - outflow;
        }
    }

    private List<FinancialRecord> loadFinancialRecordsFromBin(String fileName) {
        List<FinancialRecord> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((FinancialRecord) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }
}
