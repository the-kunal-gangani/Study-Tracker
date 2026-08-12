package com.example.studytracker.model;

import java.time.LocalDate;

public class Assignment {

    private int id;
    private String title;
    private LocalDate dueDate;
    private Priority priority;
    private AssignmentStatus status;
    private int subjectId;

    public Assignment(int id, String title, LocalDate dueDate, Priority priority,
            AssignmentStatus status, int subjectId) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public int getSubjectId() {
        return subjectId;
    }
}