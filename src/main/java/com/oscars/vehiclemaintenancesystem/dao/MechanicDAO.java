package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.model.Mechanic;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MechanicDAO {
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

}