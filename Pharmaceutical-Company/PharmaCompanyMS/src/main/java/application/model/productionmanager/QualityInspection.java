package application.model.productionmanager;

import java.io.Serializable;

public class QualityInspection implements Serializable {
    private static final long serialVersionUID = 1L;

    private String inspectionId;
    private String batchId;
    private String result;

    public QualityInspection() {}
    public QualityInspection(String inspectionId, String batchId, String result) {
        this.inspectionId = inspectionId;
        this.batchId = batchId;
        this.result = result;
    }

    public String getInspectionId() { return inspectionId; }
    public void setInspectionId(String inspectionId) { this.inspectionId = inspectionId; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
