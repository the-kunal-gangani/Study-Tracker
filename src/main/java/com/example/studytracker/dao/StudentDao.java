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
        String sql = "INSERT into students (columns: name, class_name, division)";
        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getClassName());
            stmt.setString(3, student.getDivision());
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

                Student student = new Student(id, class_name, name, division);
                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch Students.", e);
        }
        return students;
    }

    
}
