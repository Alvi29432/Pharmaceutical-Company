package application.model.accountant;

import java.io.Serializable;

public class Receivable implements Serializable {
    private static final long serialVersionUID = 1L;

    private String receivableId;
    private String customer;
    private double amount;
    private String dueDate;
    private String status;

    public Receivable() {
        this.status = "Open";
        this.dueDate = "";
    }

    public Receivable(String receivableId, String customer, double amount, String dueDate, String status) {
        this.receivableId = receivableId;
        this.customer = customer;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getReceivableId() { return receivableId; }
    public void setReceivableId(String receivableId) { this.receivableId = receivableId; }
    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
