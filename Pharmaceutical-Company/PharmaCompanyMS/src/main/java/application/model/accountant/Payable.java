package application.model.accountant;

import java.io.Serializable;

public class Payable implements Serializable {
    private static final long serialVersionUID = 1L;

    private String payableId;
    private String vendor;
    private double amount;
    private String dueDate;
    private String status;

    public Payable() {
        this.status = "Open";
        this.dueDate = "";
    }

    public Payable(String payableId, String vendor, double amount, String dueDate, String status) {
        this.payableId = payableId;
        this.vendor = vendor;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getPayableId() { return payableId; }
    public void setPayableId(String payableId) { this.payableId = payableId; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
