package com.example.studytracker;

import com.example.studytracker.dao.SubjectDao;
import com.example.studytracker.db.DatabaseManager;
import com.example.studytracker.model.Subject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeTables();

        SubjectDao subjectDao = new SubjectDao(dbManager);

        ListView<String> listView = new ListView<>();
        for (Subject s : subjectDao.getAllSubjects()) {
            listView.getItems().add(s.getName());
        }

        TextField nameField = new TextField();
        nameField.setPromptText("Subject Name");

        Button addButton = new Button("Add Subject");
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            if (name.isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Blank Values Found!!!").showAndWait();
                return;
            }
            Subject subject = new Subject(0, name, "");
            try {
                subjectDao.addSubject(subject);
                new Alert(Alert.AlertType.INFORMATION, "Data Submitted Successfully!!!").showAndWait();

                listView.getItems().clear();
                for (Subject s : subjectDao.getAllSubjects()) {
                    listView.getItems().add(s.getName());
                }
                nameField.clear();
            } catch (RuntimeException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to Add Subject : " + e.getMessage()).showAndWait();
            }
        });

        VBox root = new VBox(nameField, addButton, listView);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("MCA Study Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}