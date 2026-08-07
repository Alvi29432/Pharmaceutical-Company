package application.model.customer;

import java.io.Serializable;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String customerId;
    private String medicineId;
    private int quantity;

    public Order() {}
    public Order(String orderId, String customerId, String medicineId, int quantity) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.medicineId = medicineId;
        this.quantity = quantity;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
