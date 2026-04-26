package com.casewire.service;

import com.casewire.dao.ClueConnectionDAO;
import com.casewire.model.ClueConnection;

import java.util.List;
import java.util.Set;

public class InvestigationService {

    private final ClueConnectionDAO clueConnectionDAO = new ClueConnectionDAO();

    public ClueConnection checkConnection(int evidenceIdA, int evidenceIdB,
                                          List<ClueConnection> caseConnections) {
        for (ClueConnection connection : caseConnections) {
            boolean matchForward = connection.getEvidenceIdA() == evidenceIdA
                    && connection.getEvidenceIdB() == evidenceIdB;
            boolean matchReverse = connection.getEvidenceIdA() == evidenceIdB
                    && connection.getEvidenceIdB() == evidenceIdA;
            if (matchForward || matchReverse) {
                return connection;
            }
        }
        return null;
    }

    public void saveDiscoveredConnection(int userId, int connectionId) {
        clueConnectionDAO.saveDiscoveredConnection(userId, connectionId);
    }

    public Set<Integer> getDiscoveredConnectionIds(int userId) {
        return clueConnectionDAO.getDiscoveredConnectionIds(userId);
    }
}
