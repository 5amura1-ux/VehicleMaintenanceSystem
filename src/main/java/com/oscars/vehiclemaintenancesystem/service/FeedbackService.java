package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.FeedbackDAO;
import com.oscars.vehiclemaintenancesystem.model.Feedback;

import java.util.List;

public class FeedbackService {
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    public void addFeedback(String appointmentId, String customerId, String feedbackText, int rating) throws Exception {
        feedbackDAO.insertFeedback(appointmentId, customerId, feedbackText, rating);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackDAO.getAllFeedback();
    }
}