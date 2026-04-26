package com.casewire.model;

public class CaseFile {
    private int id;
    private int levelId;
    private String title;
    private String description;
    private String intro;

    public CaseFile(int id, int levelId, String title, String description, String intro) {
        this.id = id;
        this.levelId = levelId;
        this.title = title;
        this.description = description;
        this.intro = intro;
    }

    public int getId() { return id; }
    public int getLevelId() { return levelId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIntro() { return intro; }
}
