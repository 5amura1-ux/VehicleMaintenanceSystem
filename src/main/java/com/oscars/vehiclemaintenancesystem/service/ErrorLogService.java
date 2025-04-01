package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.ErrorLogDAO;
import com.oscars.vehiclemaintenancesystem.model.ErrorLog;

import java.util.List;

public class ErrorLogService {
    private final ErrorLogDAO errorLogDAO = new ErrorLogDAO();

    public List<ErrorLog> getAllErrorLogs() {
        return errorLogDAO.getAllErrorLogs();
    }
}