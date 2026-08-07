package application.model.customer;

import java.io.Serializable;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    private String invoiceId;
    private String orderId;
    private double amount;

    public Invoice() {}
    public Invoice(String invoiceId, String orderId, double amount) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
