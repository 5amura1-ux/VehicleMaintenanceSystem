package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;

@Entity
@Table(name = "Feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private String feedbackId;

    @Column(name = "appointment_id")
    private String appointmentId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "feedback_text")
    private String feedbackText;

    @Column(name = "rating")
    private int rating;

    // Getters and setters
    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}