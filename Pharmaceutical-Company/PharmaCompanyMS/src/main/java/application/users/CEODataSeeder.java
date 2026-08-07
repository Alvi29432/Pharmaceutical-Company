package application.users;

import application.model.common.*;
import application.model.chiefexecutiveofficer.*;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class CEODataSeeder {

    private CEODataSeeder() {}

    public static void seedIfEmpty() {
        seedMedicines();
        seedInventory();
        seedPolicies();
        seedBudgets();
        seedBatches();
        seedSchedules();
        seedFeedback();
        seedReviews();
        seedPerformance();
        seedFinancial();
        seedIncidents();
    }

    private static void seedMedicines() {
        if (!loadMedicinesFromBin("medicines.bin").isEmpty()) return;
        List<Medicine> list = new ArrayList<>();
        list.add(new Medicine("M-001", "Paracetamol 500mg", "Analgesic", 1.50));
        list.add(new Medicine("M-002", "Amoxicillin 500mg", "Antibiotic", 3.20));
        list.add(new Medicine("M-003", "Ibuprofen 200mg",   "Analgesic", 1.10));
        list.add(new Medicine("M-004", "Cetirizine 10mg",   "Antihistamine", 0.80));
        list.add(new Medicine("M-005", "Metformin 500mg",   "Antidiabetic",  2.40));
        saveMedicinesToBin("medicines.bin", list);
    }

    private static void seedInventory() {
        if (!loadInventoryFromBin("inventory.bin").isEmpty()) return;
        List<InventoryItem> list = new ArrayList<>();
        list.add(new InventoryItem("I-001", "M-001", 1200));
        list.add(new InventoryItem("I-002", "M-002",  450));
        list.add(new InventoryItem("I-003", "M-003",   80));   // low
        list.add(new InventoryItem("I-004", "M-004",  300));
        list.add(new InventoryItem("I-005", "M-005",    0));   // out
        saveInventoryToBin("inventory.bin", list);
    }

    private static void seedPolicies() {
        if (!loadPoliciesFromBin("policies.bin").isEmpty()) return;
        List<Policy> list = new ArrayList<>();
        list.add(new Policy("P-001", "Remote Work Policy", "Allow 2 remote days per week for office staff.", "HR"));
        list.add(new Policy("P-002", "Vendor Audit Policy", "Annual audit of all raw-material vendors.", "Compliance"));
        savePoliciesToBin("policies.bin", list);
    }

    private static void seedBudgets() {
        if (!loadBudgetsFromBin("budgets.bin").isEmpty()) return;
        List<Budget> list = new ArrayList<>();
        list.add(new Budget("B-001", "Production", 250000.0, "New batch line tooling."));
        list.add(new Budget("B-002", "Marketing",   80000.0, "Q3 product launch campaign."));
        list.add(new Budget("B-003", "IT",          45000.0, "Server room UPS replacement."));
        saveBudgetsToBin("budgets.bin", list);
    }

    private static void seedBatches() {
        if (!loadBatchesFromBin("batches.bin").isEmpty()) return;
        List<ProductionBatch> list = new ArrayList<>();
        list.add(new ProductionBatch("PB-001", "M-001", 5000));
        list.add(new ProductionBatch("PB-002", "M-002", 2000));
        list.add(new ProductionBatch("PB-003", "M-004", 1500));
        list.add(new ProductionBatch("PB-004", "M-005", 3000));
        saveBatchesToBin("batches.bin", list);
    }

    private static void seedSchedules() {
        if (!loadSchedulesFromBin("schedules.bin").isEmpty()) return;
        List<ProductionSchedule> list = new ArrayList<>();
        list.add(new ProductionSchedule("S-001", "PB-001", "2026-08-01"));
        list.add(new ProductionSchedule("S-002", "PB-002", "2026-08-05"));
        list.add(new ProductionSchedule("S-003", "PB-003", "2026-08-10"));
        list.add(new ProductionSchedule("S-004", "PB-004", "2026-08-15"));
        saveSchedulesToBin("schedules.bin", list);
    }

    private static void seedFeedback() {
        if (!loadFeedbackFromBin("feedback.bin").isEmpty()) return;
        List<Feedback> list = new ArrayList<>();
        list.add(new Feedback("F-001", "C-001", "Fast delivery, good packaging."));
        list.add(new Feedback("F-002", "C-002", "Website was hard to navigate."));
        list.add(new Feedback("F-003", "C-003", "Helpful customer support."));
        saveFeedbackToBin("feedback.bin", list);
    }

    private static void seedReviews() {
        if (!loadReviewsFromBin("reviews.bin").isEmpty()) return;
        List<Review> list = new ArrayList<>();
        list.add(new Review("R-001", "M-001", 5));
        list.add(new Review("R-002", "M-001", 4));
        list.add(new Review("R-003", "M-002", 3));
        list.add(new Review("R-004", "M-003", 5));
        list.add(new Review("R-005", "M-004", 4));
        saveReviewsToBin("reviews.bin", list);
    }

    private static void seedPerformance() {
        if (!loadPerformanceFromBin("performance.bin").isEmpty()) return;
        List<EmployeePerformance> list = new ArrayList<>();
        list.add(new EmployeePerformance("E-001", "2026-Q2", 4.5));
        list.add(new EmployeePerformance("E-002", "2026-Q2", 3.8));
        list.add(new EmployeePerformance("E-003", "2026-Q2", 4.2));
        list.add(new EmployeePerformance("E-004", "2026-Q2", 3.5));
        list.add(new EmployeePerformance("E-005", "2026-Q2", 4.9));
        savePerformanceToBin("performance.bin", list);
    }

    private static void seedFinancial() {
        if (!loadFinancialFromBin("financial.bin").isEmpty()) return;
        List<FinancialRecord> list = new ArrayList<>();
        list.add(new FinancialRecord("FR-001", "Revenue",  185000.0));
        list.add(new FinancialRecord("FR-002", "Expense",   62000.0));
        list.add(new FinancialRecord("FR-003", "Revenue",  220000.0));
        list.add(new FinancialRecord("FR-004", "Expense",   78500.0));
        list.add(new FinancialRecord("FR-005", "Tax",       24000.0));
        saveFinancialToBin("financial.bin", list);
    }

    private static void seedIncidents() {
        if (!loadIncidentsFromBin("incidents.bin").isEmpty()) return;
        List<Incident> list = new ArrayList<>();
        list.add(new Incident("IN-001", "Minor spill in packaging area â€” cleaned up."));
        list.add(new Incident("IN-002", "Temperature alarm in cold storage â€” resolved."));
        list.add(new Incident("IN-003", "Late delivery from supplier B â€” logged with procurement."));
        saveIncidentsToBin("incidents.bin", list);
    }

    private static <T> List<T> loadList(String filename, Class<T> type) {
        List<T> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (type.isInstance(obj)) out.add(type.cast(obj));
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private static <T> void saveList(String filename, List<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            for (T item : list) oos.writeObject(item);
        } catch (IOException ignored) {}
    }

    private static List<Medicine> loadMedicinesFromBin(String fn) { return loadList(fn, Medicine.class); }
    private static void saveMedicinesToBin(String fn, List<Medicine> list) { saveList(fn, list); }
    private static List<InventoryItem> loadInventoryFromBin(String fn) { return loadList(fn, InventoryItem.class); }
    private static void saveInventoryToBin(String fn, List<InventoryItem> list) { saveList(fn, list); }
    private static List<Policy> loadPoliciesFromBin(String fn) { return loadList(fn, Policy.class); }
    private static void savePoliciesToBin(String fn, List<Policy> list) { saveList(fn, list); }
    private static List<Budget> loadBudgetsFromBin(String fn) { return loadList(fn, Budget.class); }
    private static void saveBudgetsToBin(String fn, List<Budget> list) { saveList(fn, list); }
    private static List<ProductionBatch> loadBatchesFromBin(String fn) { return loadList(fn, ProductionBatch.class); }
    private static void saveBatchesToBin(String fn, List<ProductionBatch> list) { saveList(fn, list); }
    private static List<ProductionSchedule> loadSchedulesFromBin(String fn) { return loadList(fn, ProductionSchedule.class); }
    private static void saveSchedulesToBin(String fn, List<ProductionSchedule> list) { saveList(fn, list); }
    private static List<Feedback> loadFeedbackFromBin(String fn) { return loadList(fn, Feedback.class); }
    private static void saveFeedbackToBin(String fn, List<Feedback> list) { saveList(fn, list); }
    private static List<Review> loadReviewsFromBin(String fn) { return loadList(fn, Review.class); }
    private static void saveReviewsToBin(String fn, List<Review> list) { saveList(fn, list); }
    private static List<EmployeePerformance> loadPerformanceFromBin(String fn) { return loadList(fn, EmployeePerformance.class); }
    private static void savePerformanceToBin(String fn, List<EmployeePerformance> list) { saveList(fn, list); }
    private static List<FinancialRecord> loadFinancialFromBin(String fn) { return loadList(fn, FinancialRecord.class); }
    private static void saveFinancialToBin(String fn, List<FinancialRecord> list) { saveList(fn, list); }
    private static List<Incident> loadIncidentsFromBin(String fn) { return loadList(fn, Incident.class); }
    private static void saveIncidentsToBin(String fn, List<Incident> list) { saveList(fn, list); }
}
