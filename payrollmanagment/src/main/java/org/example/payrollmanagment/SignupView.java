package org.example.payrollmanagment;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class SignupView {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final Stage stage;
    private final UserDAO userDAO;

    public SignupView(Stage stage, UserDAO userDAO) {
        this.stage = stage;
        this.userDAO = userDAO;
    }

    public Scene getScene() {
        Label title = new Label("Create Account");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2c3e50"));

        Label subtitle = new Label("Register for the Payroll Management System");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web("#7f8c8d"));

        VBox header = new VBox(4, title, subtitle);
        header.setAlignment(Pos.CENTER);

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        styleField(fullNameField);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleField(emailField);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleField(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (min. 6 characters)");
        styleField(passwordField);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        styleField(confirmPasswordField);

        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.web("#e74c3c"));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(320);

        Button signUpButton = new Button("Sign Up");
        signUpButton.setDefaultButton(true);
        signUpButton.setMaxWidth(Double.MAX_VALUE);
        signUpButton.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;" +
                        "-fx-background-radius: 6; -fx-padding: 10 0 10 0; -fx-cursor: hand;");

        signUpButton.setOnAction(e -> {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            String error = validate(fullName, email, username, password, confirmPassword);
            if (error != null) {
                statusLabel.setTextFill(Color.web("#e74c3c"));
                statusLabel.setText(error);
                return;
            }

            boolean created = userDAO.registerUser(username, password, fullName, email);
            if (!created) {
                statusLabel.setTextFill(Color.web("#e74c3c"));
                statusLabel.setText("That username is already taken.");
                return;
            }

            statusLabel.setTextFill(Color.web("#27ae60"));
            statusLabel.setText("Account created! Redirecting to login...");

            stage.setScene(new LoginView(stage, userDAO).getScene());
        });

        Hyperlink backToLoginLink = new Hyperlink("Already have an account? Log In");
        backToLoginLink.setOnAction(e ->
                stage.setScene(new LoginView(stage, userDAO).getScene()));

        VBox form = new VBox(12,
                fullNameField, emailField, usernameField,
                passwordField, confirmPasswordField,
                signUpButton, statusLabel, backToLoginLink);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(320);

        VBox card = new VBox(20, header, form);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(380);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 4);");

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #dfe9f3, #ffffff);");
        root.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        return new Scene(scrollPane, 480, 620);
    }

    private String validate(String fullName, String email, String username,
                            String password, String confirmPassword) {
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            return "Please fill in all fields.";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Please enter a valid email address.";
        }
        if (username.length() < 3) {
            return "Username must be at least 3 characters.";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        return null;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(38);
        field.setStyle(
                "-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #dcdde1;" +
                        "-fx-padding: 6 10 6 10;");
    }
}