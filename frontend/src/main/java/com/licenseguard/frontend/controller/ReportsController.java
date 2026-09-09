package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.api.AssignmentApiClient;
import com.licenseguard.frontend.api.LicenseApiClient;
import com.licenseguard.frontend.api.SoftwareApiClient;
import com.licenseguard.frontend.model.AssignmentModel;
import com.licenseguard.frontend.model.LicenseModel;
import com.licenseguard.frontend.model.SoftwareModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ReportsController {

    @FXML private Label lblTotalCapacity;
    @FXML private Label lblAssignedSeats;
    @FXML private Label lblUnassignedSeats;
    @FXML private Label lblUtilizationRate;

    @FXML private TableView<SoftwareAuditRow> softwareSummaryTable;
    @FXML private TableColumn<SoftwareAuditRow, String> colSoftwareName;
    @FXML private TableColumn<SoftwareAuditRow, String> colVendorName;
    @FXML private TableColumn<SoftwareAuditRow, Integer> colTotalLicenses;
    @FXML private TableColumn<SoftwareAuditRow, Integer> colTotalSeats;
    @FXML private TableColumn<SoftwareAuditRow, Integer> colAvailableSeats;
    @FXML private TableColumn<SoftwareAuditRow, Integer> colAssignedCount;

    @FXML private TableView<LicenseModel> expiringLicensesTable;
    @FXML private TableColumn<LicenseModel, String> colExpSoftware;
    @FXML private TableColumn<LicenseModel, String> colExpKey;
    @FXML private TableColumn<LicenseModel, LocalDate> colExpExpiry;
    @FXML private TableColumn<LicenseModel, Integer> colExpAvailable;
    @FXML private TableColumn<LicenseModel, String> colExpStatus;

    private final LicenseApiClient licenseApiClient = new LicenseApiClient();
    private final SoftwareApiClient softwareApiClient = new SoftwareApiClient();
    private final AssignmentApiClient assignmentApiClient = new AssignmentApiClient();

    @FXML
    public void initialize() {
        setupTables();
        loadData();
    }

    private void setupTables() {
        colSoftwareName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSoftwareName()));
        colVendorName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVendorName()));
        colTotalLicenses.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalLicenses()).asObject());
        colTotalSeats.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalSeats()).asObject());
        colAvailableSeats.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAvailableSeats()).asObject());
        colAssignedCount.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAssignedCount()).asObject());

        colExpSoftware.setCellValueFactory(new PropertyValueFactory<>("softwareName"));
        colExpKey.setCellValueFactory(new PropertyValueFactory<>("licenseKey"));
        colExpExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colExpAvailable.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
        colExpStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colExpStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("EXPIRING");
                    getStyleClass().removeAll("badge-expiring");
                    getStyleClass().add("badge-expiring");
                }
            }
        });
    }

    @FXML
    public void loadData() {
        CompletableFuture<List<LicenseModel>> licFuture = licenseApiClient.getAllLicenses();
        CompletableFuture<List<SoftwareModel>> swFuture = softwareApiClient.getAllSoftware();
        CompletableFuture<List<AssignmentModel>> asgnFuture = assignmentApiClient.getAllAssignments();

        CompletableFuture.allOf(licFuture, swFuture, asgnFuture).thenRun(() -> {
            try {
                List<LicenseModel> licenses = licFuture.get();
                List<SoftwareModel> softwareList = swFuture.get();
                List<AssignmentModel> assignments = asgnFuture.get();

                Platform.runLater(() -> {
                    processMetrics(licenses, assignments);
                    processSoftwareAudit(softwareList, licenses, assignments);
                    processExpiringLicenses(licenses);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void processMetrics(List<LicenseModel> licenses, List<AssignmentModel> assignments) {
        if (licenses == null) return;
        int totalCapacity = licenses.stream().mapToInt(l -> l.getTotalSeats() != null ? l.getTotalSeats() : 0).sum();
        int unassignedSeats = licenses.stream().mapToInt(l -> l.getAvailableSeats() != null ? l.getAvailableSeats() : 0).sum();
        int assignedSeats = Math.max(0, totalCapacity - unassignedSeats);

        double rate = totalCapacity > 0 ? ((double) assignedSeats / totalCapacity) * 100.0 : 0.0;

        lblTotalCapacity.setText(String.valueOf(totalCapacity));
        lblAssignedSeats.setText(String.valueOf(assignedSeats));
        lblUnassignedSeats.setText(String.valueOf(unassignedSeats));
        lblUtilizationRate.setText(String.format("%.1f%%", rate));
    }

    private void processSoftwareAudit(List<SoftwareModel> softwareList, List<LicenseModel> licenses, List<AssignmentModel> assignments) {
        if (softwareList == null || licenses == null) return;

        Map<Integer, List<LicenseModel>> softwareLicensesMap = licenses.stream()
                .filter(l -> l.getSoftwareId() != null)
                .collect(Collectors.groupingBy(LicenseModel::getSoftwareId));

        List<SoftwareAuditRow> rows = softwareList.stream().map(s -> {
            List<LicenseModel> sLic = softwareLicensesMap.getOrDefault(s.getSoftwareId(), List.of());
            int totalLic = sLic.size();
            int totalSeats = sLic.stream().mapToInt(l -> l.getTotalSeats() != null ? l.getTotalSeats() : 0).sum();
            int availSeats = sLic.stream().mapToInt(l -> l.getAvailableSeats() != null ? l.getAvailableSeats() : 0).sum();
            int assignedCount = totalSeats - availSeats;

            return new SoftwareAuditRow(s.getSoftwareName(), s.getVendorName() != null ? s.getVendorName() : "N/A", totalLic, totalSeats, availSeats, assignedCount);
        }).collect(Collectors.toList());

        softwareSummaryTable.setItems(FXCollections.observableArrayList(rows));
    }

    private void processExpiringLicenses(List<LicenseModel> licenses) {
        if (licenses == null) return;
        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);

        List<LicenseModel> expiring = licenses.stream().filter(l -> l.getExpiryDate() != null &&
                !l.getExpiryDate().isBefore(today) &&
                !l.getExpiryDate().isAfter(in30Days)).collect(Collectors.toList());

        expiringLicensesTable.setItems(FXCollections.observableArrayList(expiring));
    }

    public static class SoftwareAuditRow {
        private final String softwareName;
        private final String vendorName;
        private final int totalLicenses;
        private final int totalSeats;
        private final int availableSeats;
        private final int assignedCount;

        public SoftwareAuditRow(String softwareName, String vendorName, int totalLicenses, int totalSeats, int availableSeats, int assignedCount) {
            this.softwareName = softwareName;
            this.vendorName = vendorName;
            this.totalLicenses = totalLicenses;
            this.totalSeats = totalSeats;
            this.availableSeats = availableSeats;
            this.assignedCount = assignedCount;
        }

        public String getSoftwareName() { return softwareName; }
        public String getVendorName() { return vendorName; }
        public int getTotalLicenses() { return totalLicenses; }
        public int getTotalSeats() { return totalSeats; }
        public int getAvailableSeats() { return availableSeats; }
        public int getAssignedCount() { return assignedCount; }
    }
}
