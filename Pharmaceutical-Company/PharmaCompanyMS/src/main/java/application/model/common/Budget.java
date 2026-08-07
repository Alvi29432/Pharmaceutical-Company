package application.model.common;

import java.io.Serializable;

public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_PENDING  = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";

    private String budgetId;
    private String department;
    private double limit;
    private String justification;
    private String status;
    private String decisionDate;

    public Budget() {
        this.status = STATUS_PENDING;
        this.decisionDate = "";
        this.justification = "";
    }

    public Budget(String budgetId, String department, double limit, String justification) {
        this.budgetId = budgetId;
        this.department = department;
        this.limit = limit;
        this.justification = justification;
        this.status = STATUS_PENDING;
        this.decisionDate = "";
    }

    public String getBudgetId() { return budgetId; }
    public void setBudgetId(String budgetId) { this.budgetId = budgetId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public double getLimit() { return limit; }
    public void setLimit(double limit) { this.limit = limit; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDecisionDate() { return decisionDate; }
    public void setDecisionDate(String decisionDate) { this.decisionDate = decisionDate; }
}
