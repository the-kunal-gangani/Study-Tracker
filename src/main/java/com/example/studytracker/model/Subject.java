package com.example.studytracker.model;

public class Subject {

    private int id;
    private String name;
    private String instructor;

    public Subject(int id, String name, String instructor) {
        this.id = id;
        this.name = name;
        this.instructor = instructor;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
}