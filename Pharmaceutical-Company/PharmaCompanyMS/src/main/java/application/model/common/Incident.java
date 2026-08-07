package application.model.common;

import java.io.Serializable;

public class Incident implements Serializable {
    private static final long serialVersionUID = 1L;

    private String incidentId;
    private String description;

    public Incident() {}
    public Incident(String incidentId, String description) {
        this.incidentId = incidentId;
        this.description = description;
    }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
