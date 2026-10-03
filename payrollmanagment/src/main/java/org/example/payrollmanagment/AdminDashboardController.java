package org.example.payrollmanagment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalEmployeesLabel;
    @FXML private Label monthlyPayrollLabel;
    @FXML private Label pendingPaymentsLabel;
    @FXML private TableView<?> recentTable;

    private User loggedInAdmin;

    // Login থেকে এডমিনের ডেটা গ্রহণ করার মেথড
    public void initData(User admin) {
        this.loggedInAdmin = admin;
        if (welcomeLabel != null && admin != null) {
            welcomeLabel.setText("Welcome, " + admin.getFullName() + "!");
        }
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        // Dashboard Action
    }

    @FXML
    private void handleManageEmployees(ActionEvent event) {
        // Manage Employees Action
    }

    @FXML
    private void handleProcessPayroll(ActionEvent event) {
        // Process Payroll Action
    }

    @FXML
    private void handleReports(ActionEvent event) {
        // Reports Action
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Logout Action
    }
}