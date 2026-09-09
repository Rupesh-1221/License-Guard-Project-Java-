package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.api.*;
import com.licenseguard.frontend.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DashboardController {

    @FXML private Label lblTotalLicenses;
    @FXML private Label lblActiveLicenses;
    @FXML private Label lblExpiringLicenses;
    @FXML private Label lblExpiredLicenses;
    @FXML private Label lblTotalSoftware;
    @FXML private Label lblTotalUsers;
    @FXML private Label lblAvailableSeats;
    @FXML private Label lblTotalVendors;

    @FXML private TableView<AssignmentModel> recentAssignmentsTable;
    @FXML private TableColumn<AssignmentModel, String> colAsgnUser;
    @FXML private TableColumn<AssignmentModel, String> colAsgnSoftware;
    @FXML private TableColumn<AssignmentModel, LocalDate> colAsgnDate;
    @FXML private TableColumn<AssignmentModel, String> colAsgnStatus;

    @FXML private TableView<RenewalModel> recentRenewalsTable;
    @FXML private TableColumn<RenewalModel, String> colRenSoftware;
    @FXML private TableColumn<RenewalModel, LocalDate> colRenNewExpiry;
    @FXML private TableColumn<RenewalModel, Number> colRenCost;
    @FXML private TableColumn<RenewalModel, LocalDate> colRenDate;

    private final LicenseApiClient licenseApiClient = new LicenseApiClient();
    private final SoftwareApiClient softwareApiClient = new SoftwareApiClient();
    private final UserApiClient userApiClient = new UserApiClient();
    private final VendorApiClient vendorApiClient = new VendorApiClient();
    private final AssignmentApiClient assignmentApiClient = new AssignmentApiClient();
    private final RenewalApiClient renewalApiClient = new RenewalApiClient();

    @FXML
    public void initialize() {
        setupTables();
        loadDashboardData();
    }

    private void setupTables() {
        colAsgnUser.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colAsgnSoftware.setCellValueFactory(new PropertyValueFactory<>("softwareName"));
        colAsgnDate.setCellValueFactory(new PropertyValueFactory<>("assignedDate"));
        colAsgnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colAsgnStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    getStyleClass().removeAll("badge-active", "badge-suspended");
                    if ("ACTIVE".equalsIgnoreCase(item)) {
                        getStyleClass().add("badge-active");
                    } else {
                        getStyleClass().add("badge-suspended");
                    }
                }
            }
        });

        colRenSoftware.setCellValueFactory(new PropertyValueFactory<>("softwareName"));
        colRenNewExpiry.setCellValueFactory(new PropertyValueFactory<>("newExpiryDate"));
        colRenCost.setCellValueFactory(new PropertyValueFactory<>("renewalCost"));
        colRenDate.setCellValueFactory(new PropertyValueFactory<>("renewalDate"));
    }

    @FXML
    public void loadDashboardData() {
        CompletableFuture.allOf(
                licenseApiClient.getAllLicenses().thenAccept(this::processLicenses),
                softwareApiClient.getAllSoftware().thenAccept(this::processSoftware),
                userApiClient.getAllUsers().thenAccept(this::processUsers),
                vendorApiClient.getAllVendors().thenAccept(this::processVendors),
                assignmentApiClient.getAllAssignments().thenAccept(this::processAssignments),
                renewalApiClient.getAllRenewals().thenAccept(this::processRenewals)
        );
    }

    private void processLicenses(List<LicenseModel> licenses) {
        Platform.runLater(() -> {
            if (licenses == null) return;
            int total = licenses.size();
            long active = licenses.stream().filter(l -> "ACTIVE".equalsIgnoreCase(l.getStatus())).count();
            LocalDate today = LocalDate.now();
            LocalDate in30Days = today.plusDays(30);

            long expiring = licenses.stream().filter(l -> l.getExpiryDate() != null &&
                    !l.getExpiryDate().isBefore(today) &&
                    !l.getExpiryDate().isAfter(in30Days)).count();

            long expired = licenses.stream().filter(l -> l.getExpiryDate() != null &&
                    l.getExpiryDate().isBefore(today)).count();

            int availableSeats = licenses.stream().mapToInt(l -> l.getAvailableSeats() != null ? l.getAvailableSeats() : 0).sum();

            lblTotalLicenses.setText(String.valueOf(total));
            lblActiveLicenses.setText(String.valueOf(active));
            lblExpiringLicenses.setText(String.valueOf(expiring));
            lblExpiredLicenses.setText(String.valueOf(expired));
            lblAvailableSeats.setText(String.valueOf(availableSeats));
        });
    }

    private void processSoftware(List<SoftwareModel> softwareList) {
        Platform.runLater(() -> {
            lblTotalSoftware.setText(softwareList != null ? String.valueOf(softwareList.size()) : "0");
        });
    }

    private void processUsers(List<UserModel> users) {
        Platform.runLater(() -> {
            lblTotalUsers.setText(users != null ? String.valueOf(users.size()) : "0");
        });
    }

    private void processVendors(List<VendorModel> vendors) {
        Platform.runLater(() -> {
            lblTotalVendors.setText(vendors != null ? String.valueOf(vendors.size()) : "0");
        });
    }

    private void processAssignments(List<AssignmentModel> assignments) {
        Platform.runLater(() -> {
            if (assignments != null && recentAssignmentsTable != null) {
                int limit = Math.min(assignments.size(), 5);
                recentAssignmentsTable.setItems(FXCollections.observableArrayList(assignments.subList(0, limit)));
            }
        });
    }

    private void processRenewals(List<RenewalModel> renewals) {
        Platform.runLater(() -> {
            if (renewals != null && recentRenewalsTable != null) {
                int limit = Math.min(renewals.size(), 5);
                recentRenewalsTable.setItems(FXCollections.observableArrayList(renewals.subList(0, limit)));
            }
        });
    }
}
