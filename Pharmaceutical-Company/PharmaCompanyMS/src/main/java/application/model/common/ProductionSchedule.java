package application.model.common;

import java.io.Serializable;

public class ProductionSchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private String scheduleId;
    private String batchId;
    private String date;

    public ProductionSchedule() {}
    public ProductionSchedule(String scheduleId, String batchId, String date) {
        this.scheduleId = scheduleId;
        this.batchId = batchId;
        this.date = date;
    }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
