package com.casewire.service;

import com.casewire.dao.CaseDAO;
import com.casewire.dao.ClueConnectionDAO;
import com.casewire.dao.EvidenceDAO;
import com.casewire.dao.LevelDAO;
import com.casewire.dao.ProgressDAO;
import com.casewire.dao.SolutionDAO;
import com.casewire.dao.SuspectDAO;
import com.casewire.model.CaseFile;
import com.casewire.model.ClueConnection;
import com.casewire.model.Evidence;
import com.casewire.model.Level;
import com.casewire.model.PlayerProgress;
import com.casewire.model.Solution;
import com.casewire.model.Suspect;

import java.util.List;

public class CaseService {

    private final CaseDAO caseDAO = new CaseDAO();
    private final SuspectDAO suspectDAO = new SuspectDAO();
    private final EvidenceDAO evidenceDAO = new EvidenceDAO();
    private final ClueConnectionDAO clueDAO = new ClueConnectionDAO();
    private final SolutionDAO solutionDAO = new SolutionDAO();
    private final LevelDAO levelDAO = new LevelDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();

    public List<Level> getAllLevels() { return levelDAO.getAllLevels(); }
    public List<CaseFile> getCasesByLevel(int levelId) { return caseDAO.getCasesByLevel(levelId); }
    public List<CaseFile> getAllCases() { return caseDAO.getAllCases(); }
    public CaseFile getCaseById(int caseId) { return caseDAO.getCaseById(caseId); }
    public List<Suspect> getSuspects(int caseId) { return suspectDAO.getSuspectsByCase(caseId); }
    public List<Evidence> getEvidence(int caseId) { return evidenceDAO.getEvidenceByCase(caseId); }
    public List<ClueConnection> getClueConnections(int caseId) { return clueDAO.getConnectionsByCase(caseId); }
    public Solution getSolution(int caseId) { return solutionDAO.getSolutionByCase(caseId); }
    public PlayerProgress getProgress(int userId, int caseId) { return progressDAO.getProgressByCase(userId, caseId); }
    public List<PlayerProgress> getAllProgress(int userId) { return progressDAO.getAllProgress(userId); }
}
