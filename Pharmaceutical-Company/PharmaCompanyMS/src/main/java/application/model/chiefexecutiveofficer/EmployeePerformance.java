package application.model.chiefexecutiveofficer;

import java.io.Serializable;

public class EmployeePerformance implements Serializable {
    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String period;
    private double score;

    public EmployeePerformance() {}
    public EmployeePerformance(String employeeId, String period, double score) {
        this.employeeId = employeeId;
        this.period = period;
        this.score = score;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}
