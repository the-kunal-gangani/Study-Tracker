package com.example.studytracker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:study_tracker.db";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void initializeTables() {

        String createStudents = """
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    class_name TEXT NOT NULL,
                    division TEXT NOT NULL
                )
                """;

        String createNotes = """
                CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT,
                    class_name TEXT NOT NULL,
                    division TEXT NOT NULL,
                    subject_id INTEGER NOT NULL,
                    FOREIGN KEY (subject_id) REFERENCES subjects(id)
                )
                """;

        String createSubjects = """
                CREATE TABLE IF NOT EXISTS subjects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    instructor TEXT
                )
                """;

        String createAssignments = """
                CREATE TABLE IF NOT EXISTS assignments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    due_date TEXT NOT NULL,
                    priority TEXT NOT NULL,
                    status TEXT NOT NULL,
                    subject_id INTEGER NOT NULL,
                    FOREIGN KEY (subject_id) REFERENCES subjects(id)
                )
                """;

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {

            stmt.execute(createSubjects);
            stmt.execute(createAssignments);
            stmt.execute(createNotes);
            stmt.execute(createStudents);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database tables", e);
        }
    }
}