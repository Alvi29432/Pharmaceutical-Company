package application.model.common;

import java.io.Serializable;

public class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemId;
    private String medicineId;
    private int stock;

    public InventoryItem() {}
    public InventoryItem(String itemId, String medicineId, int stock) {
        this.itemId = itemId;
        this.medicineId = medicineId;
        this.stock = stock;
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
