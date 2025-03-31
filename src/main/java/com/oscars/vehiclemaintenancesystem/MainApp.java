package com.oscars.vehiclemaintenancesystem;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
        primaryStage.setTitle("Vehicle Maintenance & Service Management System");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    @Override
    public void stop() {
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}