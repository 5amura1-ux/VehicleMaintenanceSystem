package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.MechanicDAO;
import com.oscars.vehiclemaintenancesystem.model.Mechanic;

import java.util.ArrayList;
import java.util.List;

public class MechanicService {
    private MechanicDAO mechanicDAO = new MechanicDAO();

    public List<Mechanic> getAllMechanics() {
        try {
            return mechanicDAO.getAllMechanics();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error retrieving mechanics: " + e.getMessage());
            return new ArrayList<>(); // Return empty list on error
        }
    }
}