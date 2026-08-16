package com.example.studytracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.studytracker.db.DatabaseManager;
import com.example.studytracker.model.Student;

public class StudentDao {
    private final DatabaseManager dbManager;

    public StudentDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addStudent(Student student) {
        String sql = "INSERT into students (name, class_name, division) VALUES (?,?,?)";
        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getClassName());
            stmt.setString(3, student.getDivision());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("....", e);
        }
    }

    public java.util.List<Student> getAllStudents() {
        String sql = "SELECT * from students";
        java.util.List<Student> students = new java.util.ArrayList<>();

        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String class_name = rs.getString("class_name");
                String division = rs.getString("division");

                Student student = new Student(id, name, class_name, division);
                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch Students.", e);
        }
        return students;
    }

    public java.util.Optional<Student> findByNameClassDivision(String name, String className, String division) {
        String sql = "SELECT * FROM students WHERE name = ? AND class_name = ? AND division = ?";

        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, className);
            stmt.setString(3, division);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    Student student = new Student(id, name, className, division);
                    return java.util.Optional.of(student);
                } else {
                    return java.util.Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find student", e);
        }
    }
}
