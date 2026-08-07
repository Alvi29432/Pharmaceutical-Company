package application.model.accountant;

import java.io.Serializable;

public class ComplianceRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recordId;
    private String regulation;
    private String description;
    private String status;

    public ComplianceRecord() {
        this.status = "Open";
    }

    public ComplianceRecord(String recordId, String regulation, String description, String status) {
        this.recordId = recordId;
        this.regulation = regulation;
        this.description = description;
        this.status = status;
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getRegulation() { return regulation; }
    public void setRegulation(String regulation) { this.regulation = regulation; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
