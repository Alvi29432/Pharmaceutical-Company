package application.model.customer;

import java.io.Serializable;

public class SupportTicket implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ticketId;
    private String customerId;
    private String issue;

    public SupportTicket() {}
    public SupportTicket(String ticketId, String customerId, String issue) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.issue = issue;
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }
}
