package application.model.accountant;

import java.io.Serializable;

public class UnitCost implements Serializable {
    private static final long serialVersionUID = 1L;

    private String medicineId;
    private double unitCost;

    public UnitCost() {}

    public UnitCost(String medicineId, double unitCost) {
        this.medicineId = medicineId;
        this.unitCost = unitCost;
    }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public double getUnitCost() { return unitCost; }
    public void setUnitCost(double unitCost) { this.unitCost = unitCost; }
}
