package com.example.studytracker;

import java.time.LocalDate;

import com.example.studytracker.dao.AssignmentDao;
import com.example.studytracker.dao.SubjectDao;
import com.example.studytracker.db.DatabaseManager;
import com.example.studytracker.model.Assignment;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeTables();

        SubjectDao subjectDao = new SubjectDao(dbManager);
        AssignmentDao assignmentDao = new AssignmentDao(dbManager);

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
        listLabel.getStyleClass().add("section-heading");

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

        ListView<String> assignmentListView = new ListView<>();
        Button addAssignmentButton = new Button("Add Assignment");
        addAssignmentButton.setOnAction(event -> {
            String title = titleField.getText();
            String selectedSubjectName = subjectComboBox.getValue();
            LocalDate dueDate = dueDatePicker.getValue();
            Priority priority = priorityComboBox.getValue();
            AssignmentStatus status = statusComboBox.getValue();

            if (title.isBlank() || selectedSubjectName == null || dueDate == null || priority == null
                    || status == null) {
                new Alert(Alert.AlertType.WARNING, "Blank Values Found").showAndWait();
                return;
            }

            int subjectId = -1;
            for (Subject s : subjectDao.getAllSubjects()) {
                if (s.getName().equals(selectedSubjectName)) {
                    subjectId = s.getId();
                }
            }

            Assignment assignment = new Assignment(0, title, dueDate, priority, status, subjectId);

            try {
                assignmentDao.addAssignment(assignment);
                new Alert(Alert.AlertType.INFORMATION, "Assignment Submitted Successfully").showAndWait();

                assignmentListView.getItems().clear();
                for (Assignment a : assignmentDao.getAllAssignments()) {
                    assignmentListView.getItems().add(a.getTitle());
                }
                titleField.clear();
            } catch (RuntimeException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to Add Assignment: " + e.getMessage()).showAndWait();
            }
        });
        Label titleLabel = new Label("Assignment Title");
        Label subjectLabel = new Label("Subject");
        Label dueDateLabel = new Label("Due Date");
        Label priorityLabel = new Label("Priority");
        Label statusLabel = new Label("Status");
        Label assignmentListLabel = new Label("Your Assignments");
        assignmentlistLabel.getStyleClass().add("section-heading");
        VBox assignmentFormBox = new VBox(6, titleLabel, titleField, subjectLabel, subjectComboBox, dueDateLabel,
                dueDatePicker, priorityLabel, priorityComboBox, statusLabel, statusComboBox, addAssignmentButton);

        assignmentFormBox.setPadding(new Insets(15));

        VBox subjectsContent = new VBox(10, formBox, listLabel, listView);
        subjectsContent.setPadding(new Insets(15));
        VBox assignmentsContent = new VBox(10, assignmentFormBox, assignmentListLabel, assignmentListView);
        assignmentsContent.setPadding(new Insets(15));

        ScrollPane subjectsScroll = new ScrollPane(subjectsContent);
        subjectsScroll.setFitToWidth(true);
        ScrollPane assignmentsPane = new ScrollPane(assignmentsContent);
        assignmentsPane.setFitToWidth(true);

        Tab subjectsTab = new Tab("Subjects", subjectsScroll);
        subjectsTab.setClosable(false);
        Tab assignmentsTab = new Tab("Assignments", assignmentsPane);
        assignmentsTab.setClosable(false);

        TabPane tabPane = new TabPane(subjectsTab, assignmentsTab);

        Scene scene = new Scene(tabPane, 600, 450);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("MCA Study Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}