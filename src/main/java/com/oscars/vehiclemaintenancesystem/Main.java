package com.oscars.vehiclemaintenancesystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Welcome to Vehicle Maintenance System");
        java.net.URL fxmlLocation = getClass().getResource("/Login.fxml");
        System.out.println("FXML Location: " + fxmlLocation); // Will print null if not found
        if (fxmlLocation == null) {
            throw new RuntimeException("Unable to find Login.fxml");
        }
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        System.out.println("Login page loading");
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Vehicle Maintenance System");
        primaryStage.show();
        System.out.println("Login page loaded");
    }

    public static void main(String[] args) {
        launch(args);
    }
}