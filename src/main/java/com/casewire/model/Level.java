package com.casewire.model;

public class Level {
    private int id;
    private String name;

    public Level(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}
