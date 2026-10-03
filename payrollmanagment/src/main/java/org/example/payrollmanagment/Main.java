package org.example.payrollmanagment;



import org.example.payrollmanagment.UserDAO;
import org.example.payrollmanagment.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        UserDAO userDAO = new UserDAO();

        primaryStage.setTitle("Payroll Management System");
        Scene loginScene = new LoginView(primaryStage, userDAO).getScene();
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}