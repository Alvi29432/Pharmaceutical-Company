package application.model.chiefexecutiveofficer;

import java.io.Serializable;

public class Policy implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_PENDING  = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";

    private String policyId;
    private String title;
    private String description;
    private String submittedBy;
    private String status;
    private String decisionDate;

    public Policy() {
        this.status = STATUS_PENDING;
        this.decisionDate = "";
    }

    public Policy(String policyId, String title, String description, String submittedBy) {
        this.policyId = policyId;
        this.title = title;
        this.description = description;
        this.submittedBy = submittedBy;
        this.status = STATUS_PENDING;
        this.decisionDate = "";
    }

    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDecisionDate() { return decisionDate; }
    public void setDecisionDate(String decisionDate) { this.decisionDate = decisionDate; }
}
