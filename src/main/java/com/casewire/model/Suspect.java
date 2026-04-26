package com.casewire.model;

public class Suspect {
    private int id;
    private int caseId;
    private String name;
    private String role;
    private String alibi;
    private String description;

    public Suspect(int id, int caseId, String name, String role, String alibi, String description) {
        this.id = id;
        this.caseId = caseId;
        this.name = name;
        this.role = role;
        this.alibi = alibi;
        this.description = description;
    }

    public int getId() { return id; }
    public int getCaseId() { return caseId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getAlibi() { return alibi; }
    public String getDescription() { return description; }
}
