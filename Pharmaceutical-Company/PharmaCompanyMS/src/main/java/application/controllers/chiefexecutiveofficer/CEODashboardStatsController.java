package application.controllers.chiefexecutiveofficer;

import application.model.common.Budget;
import application.model.common.InventoryItem;
import application.model.chiefexecutiveofficer.Policy;
import application.model.common.Review;
import application.model.chiefexecutiveofficer.EmployeePerformance;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class CEODashboardStatsController extends application.controllers.DashboardBaseController {

    @FXML private Label medicinesCountLabel;
    @FXML private Label inventoryCountLabel;
    @FXML private Label batchesCountLabel;
    @FXML private Label employeesCountLabel;
    @FXML private Label pendingPoliciesLabel;
    @FXML private Label pendingBudgetsLabel;
    @FXML private Label avgRatingLabel;
    @FXML private Label lowStockLabel;

    private static final int LOW_STOCK_THRESHOLD = 50;

    @FXML
    public void initialize() {
        handleRefresh();
    }

    @FXML
    public void handleRefresh() {
        medicinesCountLabel.setText(String.valueOf(loadMedicinesFromBin("medicines.bin").size()));

        List<InventoryItem> inventory = loadInventoryFromBin("inventory.bin");
        int lowStock = 0;
        for (int i = 0; i < inventory.size(); i++) {
            InventoryItem item = inventory.get(i);
            if (item.getStock() <= LOW_STOCK_THRESHOLD) {
                lowStock++;
            }
        }
        inventoryCountLabel.setText(String.valueOf(inventory.size()));
        lowStockLabel.setText(String.valueOf(lowStock));

        batchesCountLabel.setText(String.valueOf(loadBatchesFromBin("batches.bin").size()));

        List<EmployeePerformance> performance = loadPerformanceFromBin("performance.bin");
        int employees = 0;
        for (int i = 0; i < performance.size(); i++) {
            String id = performance.get(i).getEmployeeId();
            boolean already = false;
            for (int j = 0; j < i; j++) {
                if (performance.get(j).getEmployeeId().equals(id)) { already = true; break; }
            }
            if (!already) employees++;
        }
        employeesCountLabel.setText(String.valueOf(employees));

        List<Policy> policies = loadPoliciesFromBin("policies.bin");
        int pendingPolicies = 0;
        for (int i = 0; i < policies.size(); i++) {
            Policy p = policies.get(i);
            if (Policy.STATUS_PENDING.equals(p.getStatus())) pendingPolicies++;
        }
        pendingPoliciesLabel.setText(String.valueOf(pendingPolicies));

        List<Budget> budgets = loadBudgetsFromBin("budgets.bin");
        int pendingBudgets = 0;
        for (int i = 0; i < budgets.size(); i++) {
            Budget b = budgets.get(i);
            if (Budget.STATUS_PENDING.equals(b.getStatus())) pendingBudgets++;
        }
        pendingBudgetsLabel.setText(String.valueOf(pendingBudgets));

        List<Review> reviews = loadReviewsFromBin("reviews.bin");
        if (reviews.isEmpty()) {
            avgRatingLabel.setText("0.0 / 5");
        } else {
            int total = 0;
            for (Review r : reviews) total += r.getRating();
            double avg = (double) total / reviews.size();
            avgRatingLabel.setText(String.format("%.2f / 5", avg));
        }
    }

    private List<application.model.common.Medicine> loadMedicinesFromBin(String fileName) {
        List<application.model.common.Medicine> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((application.model.common.Medicine) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private List<InventoryItem> loadInventoryFromBin(String fileName) {
        List<InventoryItem> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((InventoryItem) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private List<application.model.common.ProductionBatch> loadBatchesFromBin(String fileName) {
        List<application.model.common.ProductionBatch> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((application.model.common.ProductionBatch) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
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

    private List<Policy> loadPoliciesFromBin(String fileName) {
        List<Policy> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Policy) ois.readObject()); }
                catch (EOFException | ClassNotFoundException e) { break; }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private List<Budget> loadBudgetsFromBin(String fileName) {
        List<Budget> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try { list.add((Budget) ois.readObject()); }
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
}
