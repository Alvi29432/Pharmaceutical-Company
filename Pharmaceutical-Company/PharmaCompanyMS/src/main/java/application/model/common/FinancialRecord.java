package application.model.common;

import java.io.Serializable;

public class FinancialRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recordId;
    private String type;
    private double amount;

    public FinancialRecord() {}
    public FinancialRecord(String recordId, String type, double amount) {
        this.recordId = recordId;
        this.type = type;
        this.amount = amount;
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
