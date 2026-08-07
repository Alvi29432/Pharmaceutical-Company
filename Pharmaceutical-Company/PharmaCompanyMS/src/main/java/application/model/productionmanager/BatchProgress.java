package application.model.productionmanager;

import java.io.Serializable;

public class BatchProgress implements Serializable {
    private static final long serialVersionUID = 1L;

    private String batchId;
    private int completedUnits;
    private int percent;

    public BatchProgress() {}

    public BatchProgress(String batchId, int completedUnits, int percent) {
        this.batchId = batchId;
        this.completedUnits = completedUnits;
        this.percent = percent;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public int getCompletedUnits() { return completedUnits; }
    public void setCompletedUnits(int completedUnits) { this.completedUnits = completedUnits; }
    public int getPercent() { return percent; }
    public void setPercent(int percent) { this.percent = percent; }
}
