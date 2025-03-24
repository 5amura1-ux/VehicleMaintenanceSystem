package com.oscars.vehiclemaintenancesystem.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();

        // Set up the scene
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Clean up Hibernate resources
        com.oscars.vehiclemaintenancesystem.util.HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}