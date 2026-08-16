package com.example.studytracker;

import java.time.LocalDate;

import com.example.studytracker.dao.AssignmentDao;
import com.example.studytracker.dao.NoteDao;
import com.example.studytracker.dao.StudentDao;
import com.example.studytracker.dao.SubjectDao;
import com.example.studytracker.db.DatabaseManager;
import com.example.studytracker.model.Assignment;
import com.example.studytracker.model.AssignmentStatus;
import com.example.studytracker.model.Priority;
import com.example.studytracker.model.Student;
import com.example.studytracker.model.Subject;
import com.example.studytracker.model.Note;

import javafx.animation.PauseTransition;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {
    private DatabaseManager dbManager;
    private Student loggedInStudent;

    @Override
    public void start(Stage primaryStage) {
        dbManager = new DatabaseManager();
        dbManager.initializeTables();
        Label splashLabel = new Label("MCA Study Tracker");
        splashLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        StackPane splashRoot = new StackPane(splashLabel);
        splashRoot.setStyle("-fx-background-color: #1e1e2f;");

        Scene splashScene = new Scene(splashRoot, 400, 250);
        primaryStage.setScene(splashScene);
        primaryStage.setTitle("Loading...");
        primaryStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> showLogin(primaryStage));
        delay.play();

    }

    private void showLogin(Stage primaryStage) {
        StudentDao studentDao = new StudentDao(dbManager);

        Label nameLabel = new Label("Student's Name");
        TextField nameField = new TextField();

        Label classLabel = new Label("Student's Class");
        TextField classField = new TextField();

        Label divisionLabel = new Label("Student's Division");
        TextField divisionField = new TextField();

        Button continueButton = new Button("Continue");
        continueButton.setOnAction(event -> {
            String name = nameField.getText();
            String className = classField.getText();
            String division = divisionField.getText();

            if (name.isBlank() || className.isBlank() || division.isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Blank Values Found").showAndWait();
                return;
            }

            java.util.Optional<Student> existing = studentDao.findByNameClassDivision(name, className, division);

            Student student;
            if (existing.isPresent()) {
                student = existing.get();
            } else {
                Student newStudent = new Student(0, name, className, division);
                studentDao.addStudent(newStudent);
                student = studentDao.findByNameClassDivision(name, className, division).get();
            }

            loggedInStudent = student;
            showMainApp(primaryStage);
        });

        VBox loginBox = new VBox(10, nameLabel, nameField, classLabel, classField,
                divisionLabel, divisionField, continueButton);
        loginBox.setPadding(new Insets(20));

        Scene scene = new Scene(loginBox, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showMainApp(Stage primaryStage) {
        SubjectDao subjectDao = new SubjectDao(dbManager);
        AssignmentDao assignmentDao = new AssignmentDao(dbManager);
        NoteDao noteDao = new NoteDao(dbManager);

        ListView<String> notesView = new ListView<>();
        for (Note n : noteDao.getNotesForStudents(loggedInStudent.getClassName(), loggedInStudent.getDivision())) {
            notesView.getItems().add(n.getTitle());
        }
        Label noteTitleLabel = new Label("Note Title");
        TextField noteTitleField = new TextField();

        Label noteContentLabel = new Label("Content");
        TextField noteContentField = new TextField();

        Label noteSubjectLabel = new Label("Subject");
        ComboBox<String> noteSubjectComboBox = new ComboBox<>();
        // populate this the same way you did subjectComboBox earlier —
        // loop through subjectDao.getAllSubjects(), add each .getName()
        for (Subject s : subjectDao.getAllSubjects()) {
            noteSubjectComboBox.getItems().add(s.getName());
        }

        Button addNoteButton = new Button("Add Note");
        addNoteButton.setOnAction(event -> {
            String title = noteTitleField.getText();
            String content = noteContentField.getText();
            String selectedSubjectName = noteSubjectComboBox.getValue();

            if (title.isBlank() || selectedSubjectName == null) {
                new Alert(Alert.AlertType.WARNING, "Blank Values Found").showAndWait();
                return;
            }

            int subjectId = -1;
            for (Subject s : subjectDao.getAllSubjects()) {
                if (s.getName().equals(selectedSubjectName)) {
                    subjectId = s.getId();
                }
            }

            Note note = new Note(0, title, content, loggedInStudent.getClassName(),
                    loggedInStudent.getDivision(), subjectId);

            try {
                noteDao.addNotes(note);
                new Alert(Alert.AlertType.INFORMATION, "Note Added Successfully").showAndWait();

                notesView.getItems().clear();
                for (Note n : noteDao.getNotesForStudents(loggedInStudent.getClassName(),
                        loggedInStudent.getDivision())) {
                    notesView.getItems().add(n.getTitle());
                }
                noteTitleField.clear();
                noteContentField.clear();
            } catch (RuntimeException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to Add Note: " + e.getMessage()).showAndWait();
            }
        });
        VBox noteFormBox = new VBox(6, noteTitleLabel, noteTitleField, noteContentLabel, noteContentField,
                noteSubjectLabel, noteSubjectComboBox, addNoteButton);
        noteFormBox.setPadding(new Insets(15));

        Label notesListLabel = new Label("Your Notes");
        notesListLabel.getStyleClass().add("section-heading");

        VBox notesContent = new VBox(10, noteFormBox, notesListLabel, notesView);
        notesContent.setPadding(new Insets(15));

        ScrollPane notesScroll = new ScrollPane(notesContent);
        notesScroll.setFitToWidth(true);

        Tab notesTab = new Tab("Notes", notesScroll);
        notesTab.setClosable(false);
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
        assignmentListLabel.getStyleClass().add("section-heading");
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

        TabPane tabPane = new TabPane(subjectsTab, assignmentsTab, notesTab);

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