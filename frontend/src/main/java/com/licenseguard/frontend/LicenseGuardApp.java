package com.licenseguard.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LicenseGuardApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("LicenseGuard - Enterprise Software License Management");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        showLogin();
    }

    public static void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(LicenseGuardApp.class.getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1150, 720);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMainLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(LicenseGuardApp.class.getResource("/fxml/MainLayout.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 800);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
