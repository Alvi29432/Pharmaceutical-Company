package application.users;

import application.model.accountant.Receivable;
import application.model.accountant.ComplianceRecord;
import application.model.common.FinancialRecord;
import application.model.common.Medicine;
import application.model.accountant.Payable;
import application.model.accountant.UnitCost;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class AccountantDataSeeder {

    private AccountantDataSeeder() {}

    /** Entry point â€” called from Main before the login screen shows. */
    public static void seedIfEmpty() {
        seedRevenueAndExpenses();
        seedAccountsPayable();
        seedAccountsReceivable();
        seedCompliance();
        seedUnitCosts();
    }

    private static void seedRevenueAndExpenses() {
        if (!loadFinancialRecordsFromBin("financial_records.bin").isEmpty()) return;
        List<FinancialRecord> list = new ArrayList<>();
        // Revenue entries
        list.add(new FinancialRecord("FR-001", "REV-Sales",                 12500.00));
        list.add(new FinancialRecord("FR-002", "REV-Distribution",           4200.00));
        list.add(new FinancialRecord("FR-003", "REV-Sales",                  6800.00));
        // Expense entries
        list.add(new FinancialRecord("FR-004", "EXP-Supplies-Raw materials", 3500.00));
        list.add(new FinancialRecord("FR-005", "EXP-Utilities-Electricity",   900.00));
        list.add(new FinancialRecord("FR-006", "EXP-Salaries-Staff payroll", 4800.00));
        list.add(new FinancialRecord("FR-007", "EXP-Maintenance-Equipment",   650.00));
        saveFinancialRecordsToBin("financial_records.bin", list);
    }

    private static void seedAccountsPayable() {
        if (!loadPayablesFromBin("accounts_payable.bin").isEmpty()) return;
        List<Payable> list = new ArrayList<>();
        list.add(new Payable("AP-001", "Pharma Logistics Co.",  3200.00, "2026-08-15", "Open"));
        list.add(new Payable("AP-002", "City Power & Light",     780.00, "2026-08-05", "Open"));
        list.add(new Payable("AP-003", "PackRight Suppliers",   1450.00, "2026-07-28", "Paid"));
        savePayablesToBin("accounts_payable.bin", list);
    }

    private static void seedAccountsReceivable() {
        if (!loadReceivablesFromBin("accounts_receivable.bin").isEmpty()) return;
        List<Receivable> list = new ArrayList<>();
        list.add(new Receivable("AR-001", "GreenCare Pharmacy",  2400.00, "2026-08-10", "Open"));
        list.add(new Receivable("AR-002", "MedExpress Clinic",   1850.00, "2026-08-20", "Open"));
        list.add(new Receivable("AR-003", "Sunrise Hospital",    3100.00, "2026-07-25", "Collected"));
        saveReceivablesToBin("accounts_receivable.bin", list);
    }

    private static void seedCompliance() {
        if (!loadComplianceFromBin("compliance.bin").isEmpty()) return;
        List<ComplianceRecord> list = new ArrayList<>();
        list.add(new ComplianceRecord("CR-001", "FDA cGMP",       "Annual facility audit due Q3",         "Open"));
        list.add(new ComplianceRecord("CR-002", "HSA Storage",    "Cold-chain temperature log review",    "Open"));
        list.add(new ComplianceRecord("CR-003", "OSHA Workplace", "Updated hazard communication training", "Resolved"));
        saveComplianceToBin("compliance.bin", list);
    }

    private static void seedUnitCosts() {
        if (!loadUnitCostsFromBin("unit_costs.bin").isEmpty()) return;
        List<Medicine> meds = loadMedicinesFromBin("medicines.bin");
        List<UnitCost> list = new ArrayList<>();
        // Default override: 40% of list price. Accountant can change later.
        for (int i = 0; i < meds.size(); i++) {
            Medicine m = meds.get(i);
            list.add(new UnitCost(m.getId(), round2(m.getPrice() * 0.4)));
        }
        saveUnitCostsToBin("unit_costs.bin", list);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
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

    private static List<FinancialRecord> loadFinancialRecordsFromBin(String fn) { return loadList(fn, FinancialRecord.class); }
    private static void saveFinancialRecordsToBin(String fn, List<FinancialRecord> list) { saveList(fn, list); }
    private static List<Payable> loadPayablesFromBin(String fn) { return loadList(fn, Payable.class); }
    private static void savePayablesToBin(String fn, List<Payable> list) { saveList(fn, list); }
    private static List<Receivable> loadReceivablesFromBin(String fn) { return loadList(fn, Receivable.class); }
    private static void saveReceivablesToBin(String fn, List<Receivable> list) { saveList(fn, list); }
    private static List<ComplianceRecord> loadComplianceFromBin(String fn) { return loadList(fn, ComplianceRecord.class); }
    private static void saveComplianceToBin(String fn, List<ComplianceRecord> list) { saveList(fn, list); }
    private static List<UnitCost> loadUnitCostsFromBin(String fn) { return loadList(fn, UnitCost.class); }
    private static void saveUnitCostsToBin(String fn, List<UnitCost> list) { saveList(fn, list); }
    private static List<Medicine> loadMedicinesFromBin(String fn) { return loadList(fn, Medicine.class); }
}
