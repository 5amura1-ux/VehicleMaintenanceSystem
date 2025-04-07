package com.oscars.vehiclemaintenancesystem;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("Welcome to Vehicle Maintenance System");

        // Initialize the database connection (Hibernate SessionFactory) before loading the UI
        try {
            System.out.println("Initializing database connection...");
            HibernateUtil.getSessionFactory(); // This will trigger the SessionFactory creation
            System.out.println("Database connection initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();

            // Show an error dialog and exit the application
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Failed to Connect to Database");
            alert.setContentText("An error occurred while connecting to the database: " + e.getMessage() + "\nThe application will exit.");
            alert.showAndWait();

            // Exit the application
            Platform.exit();
            return;
        }

        // Load the initial FXML file (Login.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        System.out.println("FXML Location: " + getClass().getResource("/Login.fxml"));
        System.out.println("Login page loading");
        Parent root = loader.load();
        System.out.println("Login page loaded");

        // Create the scene with default dimensions
        Scene scene = new Scene(root, WindowConfig.DEFAULT_WINDOW_WIDTH, WindowConfig.DEFAULT_WINDOW_HEIGHT);

        // Set the stage title
        primaryStage.setTitle("Vehicle Maintenance System - Login");

        // Apply window size constraints
        primaryStage.setMinWidth(WindowConfig.MIN_WINDOW_WIDTH);
        primaryStage.setMinHeight(WindowConfig.MIN_WINDOW_HEIGHT);
        primaryStage.setMaxWidth(WindowConfig.MAX_WINDOW_WIDTH);
        primaryStage.setMaxHeight(WindowConfig.MAX_WINDOW_HEIGHT);

        // Set the scene and show the stage
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}