package application.model.common;

import java.io.Serializable;

public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    private String feedbackId;
    private String customerId;
    private String message;

    public Feedback() {}
    public Feedback(String feedbackId, String customerId, String message) {
        this.feedbackId = feedbackId;
        this.customerId = customerId;
        this.message = message;
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
