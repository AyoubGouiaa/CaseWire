package com.casewire.model;

public class Evidence {
    private int id;
    private int caseId;
    private String title;
    private String type;
    private String description;
    private String location;
    private String whyMatters;

    public Evidence(int id, int caseId, String title, String type,
                    String description, String location, String whyMatters) {
        this.id = id;
        this.caseId = caseId;
        this.title = title;
        this.type = type;
        this.description = description;
        this.location = location;
        this.whyMatters = whyMatters;
    }

    public int getId() { return id; }
    public int getCaseId() { return caseId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getWhyMatters() { return whyMatters; }
}
