package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.AuditLogDAO;
import com.oscars.vehiclemaintenancesystem.model.AuditLog;

import java.util.List;

public class AuditLogService {
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public List<AuditLog> getAllAuditLogs() {
        return auditLogDAO.getAllAuditLogs();
    }
}