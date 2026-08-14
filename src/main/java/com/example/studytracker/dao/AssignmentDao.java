package com.example.studytracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.example.studytracker.db.DatabaseManager;
import com.example.studytracker.model.Assignment;
import com.example.studytracker.model.AssignmentStatus;
import com.example.studytracker.model.Priority;

public class AssignmentDao {

    private final DatabaseManager dbManager;

    public AssignmentDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addAssignment(Assignment assignment) {
        String sql = "INSERT INTO assignments (title, due_date, priority, status, subject_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, assignment.getTitle());
            stmt.setString(2, assignment.getDueDate().toString());
            stmt.setString(3, assignment.getPriority().name());
            stmt.setString(4, assignment.getStatus().name());
            stmt.setInt(5, assignment.getSubjectId());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("....", e);
        }
    }

    public java.util.List<Assignment> getAllAssignments() {
        String sql = "SELECT * from assignments";
        java.util.List<Assignment> assignments = new java.util.ArrayList();

        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String due_date = rs.getString("due_date");
                String priority = rs.getString("priority");
                String status = rs.getString("status");
                int subject_id = rs.getInt("subject_id");

                Assignment assignment = new Assignment(
                        id,
                        title,
                        LocalDate.parse(due_date),
                        Priority.valueOf(priority),
                        AssignmentStatus.valueOf(status),
                        subject_id);
                assignments.add(assignment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("....", e);
        }
        return assignments;
    }
}