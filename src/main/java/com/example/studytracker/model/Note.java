package com.example.studytracker.model;

public class Note {
    private int id;
    private String title;
    private String content;
    private String className;
    private String division;
    private int subjectId;

    public Note(int id, String title, String content, String className, String division, int subjectId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.className = className;
        this.division = division;
        this.subjectId = subjectId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getSubjectId() {
        return subjectId;
    }
}