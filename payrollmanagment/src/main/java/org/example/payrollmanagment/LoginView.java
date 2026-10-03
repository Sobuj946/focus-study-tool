package org.example.payrollmanagment;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView {

    private final Stage stage;
    private final UserDAO userDAO;

    public LoginView(Stage stage, UserDAO userDAO) {
        this.stage = stage;
        this.userDAO = userDAO;
    }

    public Scene getScene() {
        Label title = new Label("Payroll Management System");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2c3e50"));

        Label subtitle = new Label("Sign in to your account");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web("#7f8c8d"));

        VBox header = new VBox(4, title, subtitle);
        header.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleField(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);

        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.web("#e74c3c"));
        statusLabel.setWrapText(true);

        Button loginButton = new Button("Log In");
        loginButton.setDefaultButton(true);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle(
                "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;" +
                        "-fx-background-radius: 6; -fx-padding: 10 0 10 0; -fx-cursor: hand;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter both username and password.");
                return;
            }

            User authenticated = userDAO.authenticate(username, password);
            if (authenticated != null) {
                statusLabel.setTextFill(Color.web("#27ae60"));
                statusLabel.setText("Login successful. Redirecting...");

                // Admin Dashboard FXML-এ নেভিগেট করার ফাংশন কল
                navigateToDashboard(authenticated);
            } else {
                statusLabel.setTextFill(Color.web("#e74c3c"));
                statusLabel.setText("Invalid username or password.");
            }
        });

        Hyperlink signUpLink = new Hyperlink("Don't have an account? Sign Up");
        signUpLink.setOnAction(e ->
                stage.setScene(new SignupView(stage, userDAO).getScene()));

        VBox form = new VBox(12, usernameField, passwordField, loginButton, statusLabel, signUpLink);
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

        return new Scene(root, 480, 520);
    }

    private void navigateToDashboard(User user) {
        try {
            // ১. FXML ফাইল লোড করা
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/payrollmanagment/admin-dashboard.fxml"));
            Parent root = loader.load();

            // ২. AdminDashboardController এর রেফারেন্স নিয়ে লগইন করা ইউজারের ডাটা পাঠানো
            AdminDashboardController dashboardController = loader.getController();
            dashboardController.initData(user);

            // ৩. নতুন সিন সেট করে উইন্ডো সাইজ ও রিসাইজ অন করা
            Scene scene = new Scene(root, 1000, 650);
            stage.setScene(scene);
            stage.setResizable(true); // ড্যাশবোর্ড যেন রিসাইজ করা যায়
            stage.setTitle("Admin Dashboard - Payroll Management");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading admin-dashboard.fxml. Check file path.");
        }
    }

    private void styleField(TextField field) {
        field.setPrefHeight(38);
        field.setStyle(
                "-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #dcdde1;" +
                        "-fx-padding: 6 10 6 10;");
    }
}