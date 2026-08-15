package com.example.studytracker;

import com.example.studytracker.dao.SubjectDao;
import com.example.studytracker.db.DatabaseManager;
import com.example.studytracker.model.AssignmentStatus;
import com.example.studytracker.model.Priority;
import com.example.studytracker.model.Subject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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

        Label nameLabel = new Label("Subject Name");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Data Structures");

        Label instructorLabel = new Label("Instructor");
        TextField instructorField = new TextField();
        instructorField.setPromptText("e.g. Prof. Sharma");

        Button addButton = new Button("Add Subject");
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            String instructor = instructorField.getText();
            if (name.isBlank() || instructor.isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Blank Values Found!!!").showAndWait();
                return;
            }
            Subject subject = new Subject(0, name, instructor);
            try {
                subjectDao.addSubject(subject);
                new Alert(Alert.AlertType.INFORMATION, "Data Submitted Successfully!!!").showAndWait();

                listView.getItems().clear();
                for (Subject s : subjectDao.getAllSubjects()) {
                    listView.getItems().add(s.getName());
                }
                nameField.clear();
                instructorField.clear();
            } catch (RuntimeException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to Add Subject : " + e.getMessage()).showAndWait();
            }
        });

        VBox formBox = new VBox(6, nameLabel, nameField, instructorLabel, instructorField, addButton);
        formBox.setPadding(new Insets(15));

        Label listLabel = new Label("Your Subjects");
        listLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextField titleField = new TextField();
        titleField.setPromptText("Assignment Title");

        ComboBox<String> subjectComboBox = new ComboBox<>();
        for (Subject s : subjectDao.getAllSubjects()) {
            subjectComboBox.getItems().add(s.getName());
        }

        DatePicker dueDatePicker = new DatePicker();

        ComboBox<Priority> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(Priority.values());

        ComboBox<AssignmentStatus> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(AssignmentStatus.values());

        Button addAssignmentButton = new Button("Add Assignment");

        ListView<String> assignmentListView = new ListView<>();
        Label titleLabel = new Label("Assignment Title");
        Label subjectLabel = new Label("Subject");
        Label dueDateLabel = new Label("Due Date");
        Label priorityLabel = new Label("Priority");
        Label statusLabel = new Label("Status");
        Label assignmentListLabel = new Label("Your Assignments");
        assignmentListLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox assignmentFormBox = new VBox(6, titleLabel, titleField, subjectLabel, subjectComboBox, dueDateLabel,
                dueDatePicker, priorityLabel, priorityComboBox, statusLabel, statusComboBox, addAssignmentButton);

        assignmentFormBox.setPadding(new Insets(15));

        VBox root = new VBox(10, formBox, listLabel, listView, assignmentFormBox, assignmentListLabel,
                assignmentListView);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 600, 450);

        primaryStage.setTitle("MCA Study Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}