package application.model.common;

import java.io.Serializable;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private String notificationId;
    private String recipient;
    private String message;

    public Notification() {}
    public Notification(String notificationId, String recipient, String message) {
        this.notificationId = notificationId;
        this.recipient = recipient;
        this.message = message;
    }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
