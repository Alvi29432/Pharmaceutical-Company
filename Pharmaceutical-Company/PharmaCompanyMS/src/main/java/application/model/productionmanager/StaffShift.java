package application.model.productionmanager;

import java.io.Serializable;

public class StaffShift implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String SHIFT_MORNING = "Morning";
    public static final String SHIFT_EVENING = "Evening";
    public static final String SHIFT_NIGHT   = "Night";

    private String shiftId;
    private String employeeName;
    private String date;
    private String shiftType;

    public StaffShift() {}

    public StaffShift(String shiftId, String employeeName, String date, String shiftType) {
        this.shiftId = shiftId;
        this.employeeName = employeeName;
        this.date = date;
        this.shiftType = shiftType;
    }

    public String getShiftId() { return shiftId; }
    public void setShiftId(String shiftId) { this.shiftId = shiftId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getShiftType() { return shiftType; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }
}
