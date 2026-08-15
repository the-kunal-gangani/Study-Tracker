package com.example.studytracker.model;

public class Student {

    private int id;
    private String name;
    private String className;
    private String division;

    public Student(int id, String name, String className, String division) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.division = division;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }
}