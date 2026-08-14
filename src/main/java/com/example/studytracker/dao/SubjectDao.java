package com.example.studytracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.studytracker.db.DatabaseManager;
import com.example.studytracker.model.Subject;

public class SubjectDao {

    private final DatabaseManager dbManager;

    public SubjectDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addSubject(Subject subject) {
        String sql = "INSERT INTO subjects (name, instructor) VALUES (?, ?)";
        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getInstructor());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("...", e);
        }
    }

    public java.util.List<Subject> getAllSubjects() {
        String sql = "SELECT * FROM subjects";
        java.util.List<Subject> subjects = new java.util.ArrayList<>();

        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String instructor = rs.getString("instructor");

                Subject subject = new Subject(id, name, instructor);
                subjects.add(subject);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch subjects", e);
        }

        return subjects;
    }
}