package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.Feedback;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class FeedbackDAO {
    public void insertFeedback(String appointmentId, String customerId, String feedbackText, int rating) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Feedback feedback = new Feedback();
            feedback.setAppointmentId(appointmentId);
            feedback.setCustomerId(customerId);
            feedback.setFeedbackText(feedbackText);
            feedback.setRating(rating);
            session.save(feedback);
            tx.commit();
        }
    }

    public List<Feedback> getAllFeedback() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Feedback", Feedback.class).list();
        }
    }
}