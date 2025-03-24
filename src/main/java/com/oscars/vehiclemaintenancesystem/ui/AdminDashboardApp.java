package com.oscars.vehiclemaintenancesystem.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminDashboardApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin_dashboard.fxml"));
        Parent root = loader.load();

        // Set up the scene
        Scene scene = new Scene(root, 1440, 960);
        primaryStage.setTitle("Admin Dashboard");
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