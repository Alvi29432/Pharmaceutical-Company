package application.users;

import application.model.productionmanager.BatchProgress;
import application.model.common.Incident;
import application.model.productionmanager.QualityInspection;
import application.model.productionmanager.StaffShift;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class ProductionManagerDataSeeder {

    private ProductionManagerDataSeeder() {}

    public static void seedIfEmpty() {
        seedBatchProgress();
        seedQualityInspections();
        seedStaffShifts();
        seedIncidents();
    }

    private static void seedBatchProgress() {
        if (!loadBatchProgressFromBin("batch_progress.bin").isEmpty()) return;
        List<BatchProgress> list = new ArrayList<>();
        list.add(new BatchProgress("PB-001", 2500, 50));
        list.add(new BatchProgress("PB-002",  800, 40));
        list.add(new BatchProgress("PB-003", 1500, 100));
        list.add(new BatchProgress("PB-004",  600, 20));
        saveBatchProgressToBin("batch_progress.bin", list);
    }

    private static void seedQualityInspections() {
        if (!loadQualityInspectionsFromBin("quality_inspections.bin").isEmpty()) return;
        List<QualityInspection> list = new ArrayList<>();
        list.add(new QualityInspection("QI-001", "PB-001", "Pass"));
        list.add(new QualityInspection("QI-002", "PB-002", "Fail"));
        list.add(new QualityInspection("QI-003", "PB-003", "Pass"));
        saveQualityInspectionsToBin("quality_inspections.bin", list);
    }

    private static void seedStaffShifts() {
        if (!loadStaffShiftsFromBin("staff_shifts.bin").isEmpty()) return;
        List<StaffShift> list = new ArrayList<>();
        list.add(new StaffShift("SS-001", "Alice Tan",   "2026-07-30", StaffShift.SHIFT_MORNING));
        list.add(new StaffShift("SS-002", "Bob Lee",     "2026-07-30", StaffShift.SHIFT_EVENING));
        list.add(new StaffShift("SS-003", "Carmen Diaz", "2026-07-30", StaffShift.SHIFT_NIGHT));
        list.add(new StaffShift("SS-004", "David Wong",  "2026-07-31", StaffShift.SHIFT_MORNING));
        saveStaffShiftsToBin("staff_shifts.bin", list);
    }

    private static void seedIncidents() {
        if (!loadIncidentsFromBin("incidents_pm.bin").isEmpty()) return;
        List<Incident> list = new ArrayList<>();
        list.add(new Incident("PM-IN-001", "Mixer B vibration alarm triggered during PB-002 run."));
        list.add(new Incident("PM-IN-002", "Operator reported fatigue â€” shift swap authorised."));
        saveIncidentsToBin("incidents_pm.bin", list);
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

    private static List<BatchProgress> loadBatchProgressFromBin(String fn) { return loadList(fn, BatchProgress.class); }
    private static void saveBatchProgressToBin(String fn, List<BatchProgress> list) { saveList(fn, list); }
    private static List<QualityInspection> loadQualityInspectionsFromBin(String fn) { return loadList(fn, QualityInspection.class); }
    private static void saveQualityInspectionsToBin(String fn, List<QualityInspection> list) { saveList(fn, list); }
    private static List<StaffShift> loadStaffShiftsFromBin(String fn) { return loadList(fn, StaffShift.class); }
    private static void saveStaffShiftsToBin(String fn, List<StaffShift> list) { saveList(fn, list); }
    private static List<Incident> loadIncidentsFromBin(String fn) { return loadList(fn, Incident.class); }
    private static void saveIncidentsToBin(String fn, List<Incident> list) { saveList(fn, list); }
}
