package com.licenseguard.frontend.util;

import com.licenseguard.frontend.model.ErrorResponseModel;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Map;
import java.util.Optional;

public class AlertUtils {

    public static void showInfo(String title, String message) {
        runOnFxThread(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showWarning(String title, String message) {
        runOnFxThread(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showError(String title, String message) {
        runOnFxThread(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showBackendError(String defaultTitle, Throwable throwable) {
        String msg = throwable.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = "An unexpected error occurred while communicating with the server.";
        }
        showError(defaultTitle, msg);
    }

    public static void showBackendApiError(ErrorResponseModel errorResponse) {
        runOnFxThread(() -> {
            StringBuilder sb = new StringBuilder();
            if (errorResponse.getMessage() != null && !errorResponse.getMessage().isBlank()) {
                sb.append(errorResponse.getMessage());
            } else {
                sb.append("Server returned status ").append(errorResponse.getStatus());
            }

            if (errorResponse.getValidationErrors() != null && !errorResponse.getValidationErrors().isEmpty()) {
                sb.append("\n\nValidation Details:");
                for (Map.Entry<String, String> entry : errorResponse.getValidationErrors().entrySet()) {
                    sb.append("\n• ").append(entry.getKey()).append(": ").append(entry.getValue());
                }
            }

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(errorResponse.getError() != null ? errorResponse.getError() : "API Error");
            alert.setHeaderText("Server Status " + errorResponse.getStatus());
            alert.setContentText(sb.toString());
            alert.showAndWait();
        });
    }

    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void runOnFxThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
