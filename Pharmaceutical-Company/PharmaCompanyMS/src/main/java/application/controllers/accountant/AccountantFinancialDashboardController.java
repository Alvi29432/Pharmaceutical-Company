package application.controllers.accountant;

import application.model.common.Budget;
import application.model.accountant.ComplianceRecord;
import application.model.common.FinancialRecord;
import application.model.common.Medicine;
import application.model.accountant.Payable;
import application.model.common.ProductionBatch;
import application.model.accountant.Receivable;
import application.model.accountant.UnitCost;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class AccountantFinancialDashboardController extends application.controllers.DashboardBaseController {

    @FXML private Label revenueLabel;
    @FXML private Label expenseLabel;
    @FXML private Label profitLabel;
    @FXML private Label receivableLabel;
    @FXML private Label payableLabel;
    @FXML private Label complianceLabel;
    @FXML private Label budgetLabel;
    @FXML private Label productionCostLabel;
    @FXML private Label infoLabel;

    @FXML
    public void initialize() {
        loadDashboard();
    }

    @FXML
    public void loadDashboard() {
        List<FinancialRecord> all = loadFinancialRecordsFromBin("financial_records.bin");
        double revenue = 0, expense = 0;
        for (int i = 0; i < all.size(); i++) {
            FinancialRecord r = all.get(i);
            if (r.getType() == null) continue;
            if (r.getType().startsWith("REV-")) revenue += r.getAmount();
            else if (r.getType().startsWith("EXP-")) expense += r.getAmount();
        }
        double profit = revenue - expense;

        List<Receivable> receivables = loadReceivablesFromBin("accounts_receivable.bin");
        double openReceivables = 0;
        for (int i = 0; i < receivables.size(); i++) {
            Receivable r = receivables.get(i);
            if ("Open".equalsIgnoreCase(r.getStatus())) openReceivables += r.getAmount();
        }
        List<Payable> payables = loadPayablesFromBin("accounts_payable.bin");
        double openPayables = 0;
        for (int i = 0; i < payables.size(); i++) {
            Payable p = payables.get(i);
            if ("Open".equalsIgnoreCase(p.getStatus())) openPayables += p.getAmount();
        }
        List<ComplianceRecord> compliance = loadComplianceFromBin("compliance.bin");
        int openCompliance = 0;
        for (int i = 0; i < compliance.size(); i++) {
            if (!"Resolved".equalsIgnoreCase(compliance.get(i).getStatus())) openCompliance++;
        }
        List<Budget> budgets = loadBudgetsFromBin("budgets.bin");
        int pendingBudgets = 0;
        for (int i = 0; i < budgets.size(); i++) {
            if (Budget.STATUS_PENDING.equalsIgnoreCase(budgets.get(i).getStatus())) pendingBudgets++;
        }

        List<ProductionBatch> batches = loadBatchesFromBin("batches.bin");
        List<Medicine> meds = loadMedicinesFromBin("medicines.bin");
        List<UnitCost> overrides = loadUnitCostsFromBin("unit_costs.bin");
        double productionCost = 0;
        for (int i = 0; i < batches.size(); i++) {
            ProductionBatch b = batches.get(i);
            double price = 0;
            for (int j = 0; j < meds.size(); j++) {
                if (meds.get(j).getId().equals(b.getMedicineId())) {
                    price = meds.get(j).getPrice();
                    break;
                }
            }
            double unit = price * 0.4;
            for (int j = 0; j < overrides.size(); j++) {
                if (overrides.get(j).getMedicineId().equals(b.getMedicineId())) {
                    unit = overrides.get(j).getUnitCost();
                    break;
                }
            }
            productionCost += unit * b.getQuantity();
        }

        revenueLabel.setText(String.format("%.2f", revenue));
        expenseLabel.setText(String.format("%.2f", expense));
        profitLabel.setText(String.format("%.2f", profit));
        receivableLabel.setText(String.format("%.2f", openReceivables));
        payableLabel.setText(String.format("%.2f", openPayables));
        complianceLabel.setText(String.valueOf(openCompliance));
        budgetLabel.setText(String.valueOf(pendingBudgets));
        productionCostLabel.setText(String.format("%.2f", productionCost));
        infoLabel.setText("Dashboard refreshed.");
    }

    private List<FinancialRecord> loadFinancialRecordsFromBin(String filename) {
        List<FinancialRecord> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof FinancialRecord) out.add((FinancialRecord) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private List<Receivable> loadReceivablesFromBin(String filename) {
        List<Receivable> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof Receivable) out.add((Receivable) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private List<Payable> loadPayablesFromBin(String filename) {
        List<Payable> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof Payable) out.add((Payable) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private List<ComplianceRecord> loadComplianceFromBin(String filename) {
        List<ComplianceRecord> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof ComplianceRecord) out.add((ComplianceRecord) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private List<Budget> loadBudgetsFromBin(String filename) {
        List<Budget> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof Budget) out.add((Budget) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private List<ProductionBatch> loadBatchesFromBin(String filename) {
        List<ProductionBatch> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof ProductionBatch) out.add((ProductionBatch) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private List<Medicine> loadMedicinesFromBin(String filename) {
        List<Medicine> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof Medicine) out.add((Medicine) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }

    private List<UnitCost> loadUnitCostsFromBin(String filename) {
        List<UnitCost> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof UnitCost) out.add((UnitCost) obj);
                } catch (EOFException eof) { break; } catch (ClassNotFoundException cnf) { break; }
            }
        } catch (IOException ignored) {}
        return out;
    }
}
