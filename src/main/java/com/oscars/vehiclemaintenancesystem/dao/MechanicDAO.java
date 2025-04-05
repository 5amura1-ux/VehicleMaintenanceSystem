package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.model.Mechanic;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MechanicDAO {
    public String insertMechanic(String username, String password, String firstName, String lastName, String email) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Mechanic mechanic = new Mechanic();
            mechanic.setUserId("USER" + String.format("%05d", getNextUserId(session))); // Generate user_id
            mechanic.setUsername(username);
            mechanic.setPassword(password);
            mechanic.setRoleId("ROLE00003"); // Set role to mechanic
            mechanic.setFirstName(firstName);
            mechanic.setLastName(lastName);
            mechanic.setEmail(email);
            session.save(mechanic);
            tx.commit();
            return mechanic.getUserId();
        }
    }

    public void deleteMechanic(String userId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Mechanic mechanic = session.get(Mechanic.class, userId);
            if (mechanic != null) {
                session.delete(mechanic);
            }
            tx.commit();
        }
    }

    public List<Mechanic> getAllMechanics() throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Mechanic> query = session.createQuery("FROM Mechanic WHERE roleId = 'ROLE00003'", Mechanic.class);
            List<Mechanic> mechanics = query.list();
            System.out.println("Fetched mechanics: " + mechanics.size()); // Debug log
            for (Mechanic mechanic : mechanics) {
                System.out.println("Mechanic: ID=" + mechanic.getUserId() + ", Name=" + mechanic.getFirstName() + " " + mechanic.getLastName());
            }
            return mechanics;
        }
    }

    private int getNextUserId(Session session) {
        Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Mechanic", Long.class);
        Long count = query.uniqueResult();
        return count.intValue() + 1; // Increment to get the next ID
    }
}