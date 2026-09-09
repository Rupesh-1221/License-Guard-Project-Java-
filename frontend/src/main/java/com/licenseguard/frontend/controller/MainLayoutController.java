package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.LicenseGuardApp;
import com.licenseguard.frontend.api.ApiClient;
import com.licenseguard.frontend.util.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Text headerTitle;

    @FXML
    private Label backendStatusLabel;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnDepartments;
    @FXML
    private Button btnUsers;
    @FXML
    private Button btnVendors;
    @FXML
    private Button btnSoftware;
    @FXML
    private Button btnLicenses;
    @FXML
    private Button btnAssignments;
    @FXML
    private Button btnRenewals;
    @FXML
    private Button btnReports;

    private Button currentNavButton;

    @FXML
    public void initialize() {
        NavigationManager.setContentArea(contentArea);
        currentNavButton = btnDashboard;
        checkBackendStatus();

        // Load Dashboard by default
        handleNavDashboard();
    }

    public void checkBackendStatus() {
        ApiClient.getInstance().checkBackendHealth().thenAccept(online -> {
            Platform.runLater(() -> {
                if (backendStatusLabel != null) {
                    if (online) {
                        backendStatusLabel.setText("API: CONNECTED (localhost:8080)");
                        backendStatusLabel.getStyleClass().removeAll("status-pill-offline");
                        backendStatusLabel.getStyleClass().add("status-pill-online");
                    } else {
                        backendStatusLabel.setText("API: OFFLINE (localhost:8080)");
                        backendStatusLabel.getStyleClass().removeAll("status-pill-online");
                        backendStatusLabel.getStyleClass().add("status-pill-offline");
                    }
                }
            });
        });
    }

    private void setActiveNavButton(Button button, String title) {
        if (currentNavButton != null) {
            currentNavButton.getStyleClass().remove("active");
        }
        button.getStyleClass().add("active");
        currentNavButton = button;
        if (headerTitle != null) {
            headerTitle.setText(title);
        }
    }

    @FXML
    public void handleNavDashboard() {
        setActiveNavButton(btnDashboard, "Dashboard & System Analytics");
        NavigationManager.navigateTo("/fxml/Dashboard.fxml");
    }

    @FXML
    public void handleNavDepartments() {
        setActiveNavButton(btnDepartments, "Department Management");
        NavigationManager.navigateTo("/fxml/Departments.fxml");
    }

    @FXML
    public void handleNavUsers() {
        setActiveNavButton(btnUsers, "User Management");
        NavigationManager.navigateTo("/fxml/Users.fxml");
    }

    @FXML
    public void handleNavVendors() {
        setActiveNavButton(btnVendors, "Software Vendors");
        NavigationManager.navigateTo("/fxml/Vendors.fxml");
    }

    @FXML
    public void handleNavSoftware() {
        setActiveNavButton(btnSoftware, "Software Catalog");
        NavigationManager.navigateTo("/fxml/Software.fxml");
    }

    @FXML
    public void handleNavLicenses() {
        setActiveNavButton(btnLicenses, "Software Licenses");
        NavigationManager.navigateTo("/fxml/Licenses.fxml");
    }

    @FXML
    public void handleNavAssignments() {
        setActiveNavButton(btnAssignments, "License Assignments");
        NavigationManager.navigateTo("/fxml/Assignments.fxml");
    }

    @FXML
    public void handleNavRenewals() {
        setActiveNavButton(btnRenewals, "License Renewals");
        NavigationManager.navigateTo("/fxml/Renewals.fxml");
    }

    @FXML
    public void handleNavReports() {
        setActiveNavButton(btnReports, "Reports & License Audit");
        NavigationManager.navigateTo("/fxml/Reports.fxml");
    }

    @FXML
    public void handleGlobalRefresh() {
        checkBackendStatus();
        String currentView = NavigationManager.getCurrentView();
        if (currentView != null) {
            NavigationManager.navigateTo(currentView);
        }
    }

    @FXML
    public void handleLogout() {
        LicenseGuardApp.showLogin();
    }
}
