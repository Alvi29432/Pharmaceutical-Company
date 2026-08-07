package application.model.common;

import java.io.Serializable;

public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reviewId;
    private String medicineId;
    private int rating;

    public Review() {}
    public Review(String reviewId, String medicineId, int rating) {
        this.reviewId = reviewId;
        this.medicineId = medicineId;
        this.rating = rating;
    }

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}
