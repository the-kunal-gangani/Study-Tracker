package com.example.studytracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.example.studytracker.db.DatabaseManager;
import com.example.studytracker.model.Subject;

public class SubjectDao {

    private final DatabaseManager dbManager;

    public SubjectDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addSubject(Subject subject) {
        String sql = "INSERT INTO subjects (name, instructor) VALUES (?, ?)";
        try {
            Connection conn = dbManager.connect();
            PreparedStatement stmt = conn.prepareStatement(sql);
        } catch (Exception e) {
        }
    }
}