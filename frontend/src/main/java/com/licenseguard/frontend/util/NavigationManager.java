package com.licenseguard.frontend.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class NavigationManager {
    private static StackPane contentArea;
    private static String currentView;

    public static void setContentArea(StackPane area) {
        contentArea = area;
    }

    public static String getCurrentView() {
        return currentView;
    }

    public static Object navigateTo(String fxmlPath) {
        if (contentArea == null) {
            System.err.println("NavigationManager error: Content area is not set.");
            return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
            currentView = fxmlPath;
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Navigation Error", "Could not load view file: " + fxmlPath + "\nCause: " + e.getMessage());
            return null;
        }
    }
}
