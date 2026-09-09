package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.LicenseGuardApp;
import com.licenseguard.frontend.api.ApiClient;
import com.licenseguard.frontend.util.AlertUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        checkBackendConnection();
    }

    @FXML
    public void checkBackendConnection() {
        if (statusLabel != null) {
            statusLabel.setText("Checking backend connection...");
            statusLabel.getStyleClass().removeAll("status-pill-online", "status-pill-offline");
            statusLabel.getStyleClass().add("status-pill-offline");
        }

        ApiClient.getInstance().checkBackendHealth().thenAccept(online -> {
            Platform.runLater(() -> {
                if (statusLabel != null) {
                    if (online) {
                        statusLabel.setText("API: CONNECTED (localhost:8080)");
                        statusLabel.getStyleClass().removeAll("status-pill-offline");
                        statusLabel.getStyleClass().add("status-pill-online");
                    } else {
                        statusLabel.setText("API: OFFLINE (http://localhost:8080)");
                        statusLabel.getStyleClass().removeAll("status-pill-online");
                        statusLabel.getStyleClass().add("status-pill-offline");
                    }
                }
            });
        });
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            AlertUtils.showWarning("Login Validation", "Please enter both username/email and password.");
            return;
        }

        // Authentication placeholder (Spring Security ready)
        LicenseGuardApp.showMainLayout();
    }
}
