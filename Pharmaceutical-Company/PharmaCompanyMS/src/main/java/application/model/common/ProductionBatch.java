package application.model.common;

import java.io.Serializable;

public class ProductionBatch implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_PLANNED    = "Planned";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_COMPLETED   = "Completed";

    private String batchId;
    private String medicineId;
    private int quantity;
    private String status;

    public ProductionBatch() {
        this.status = STATUS_PLANNED;
    }

    public ProductionBatch(String batchId, String medicineId, int quantity) {
        this.batchId = batchId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.status = STATUS_PLANNED;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
