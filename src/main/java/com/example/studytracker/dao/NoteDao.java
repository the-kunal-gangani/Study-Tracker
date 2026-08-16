package com.example.studytracker.dao;

import com.example.studytracker.db.DatabaseManager;
import com.example.studytracker.model.Note;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NoteDao {
    private final DatabaseManager dbManager;

    public NoteDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addNotes(Note note) {
        String sql = "INSERT INTO notes (title, content, class_name, division, subject_id) VALUES (?,?,?,?,?)";
        try (Connection conn = dbManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getContent());
            stmt.setString(3, note.getClassName());
            stmt.setString(4, note.getDivision());
            stmt.setInt(5, note.getSubjectId());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("....", e);
        }
    }

    public java.util.List<Note> getAllNotes() {
        String sql = "SELECT * from notes";
        java.util.List<Note> notes = new ArrayList<>();

        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String class_name = rs.getString("class_name");
                String division = rs.getString("division");
                int subject_id = rs.getInt("subject_id");

                Note note = new Note(id, title, content, class_name, division, subject_id);
                notes.add(note);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch all the Notes..", e);
        }
        return notes;
    }

    public java.util.List<Note> getNotesForStudents(String className, String division) {
        String sql = "SELECT * from notes WHERE class_name = ? AND division = ?";
        java.util.List<Note> notes = new ArrayList<>();

        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, className);
            stmt.setString(2, division);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // read all six columns: id, title, content, class_name, division, subject_id
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    String class_name = rs.getString("class_name");
                    String dvsn = rs.getString("division");
                    String subject_id = rs.getString("subject_id");
                    // build a Note with them (same constructor order as getAllNotes)
                    Note note = new Note(id, title, content, className, division, subject_id);
                    // add it to `notes`
                    notes.add(note);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch notes for student", e);
        }

        return notes;
    }
}
