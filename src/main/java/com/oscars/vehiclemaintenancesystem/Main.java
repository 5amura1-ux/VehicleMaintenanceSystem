package com.oscars.vehiclemaintenancesystem;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("Welcome to Vehicle Maintenance System");

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