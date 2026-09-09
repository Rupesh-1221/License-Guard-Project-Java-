package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.api.LicenseApiClient;
import com.licenseguard.frontend.api.SoftwareApiClient;
import com.licenseguard.frontend.model.LicenseModel;
import com.licenseguard.frontend.model.SoftwareModel;
import com.licenseguard.frontend.util.AlertUtils;
import com.licenseguard.frontend.util.DateFormatter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LicenseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<SoftwareModel> softwareFilter;
    @FXML private CheckBox expiringCheckBox;

    @FXML private TableView<LicenseModel> licenseTable;
    @FXML private TableColumn<LicenseModel, Integer> colId;
    @FXML private TableColumn<LicenseModel, String> colSoftware;
    @FXML private TableColumn<LicenseModel, String> colVendor;
    @FXML private TableColumn<LicenseModel, String> colKey;
    @FXML private TableColumn<LicenseModel, String> colType;
    @FXML private TableColumn<LicenseModel, String> colSeats;
    @FXML private TableColumn<LicenseModel, LocalDate> colExpiry;
    @FXML private TableColumn<LicenseModel, BigDecimal> colCost;
    @FXML private TableColumn<LicenseModel, String> colStatus;
    @FXML private TableColumn<LicenseModel, Void> colActions;

    private final LicenseApiClient apiClient = new LicenseApiClient();
    private final SoftwareApiClient softwareApiClient = new SoftwareApiClient();
    private final ObservableList<LicenseModel> masterData = FXCollections.observableArrayList();
    private FilteredList<LicenseModel> filteredData;
    private List<SoftwareModel> softwareList;

    @FXML
    public void initialize() {
        setupTable();
        setupSearchAndFilters();
        loadSoftwareList();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("licenseId"));
        colSoftware.setCellValueFactory(new PropertyValueFactory<>("softwareName"));
        colVendor.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        colKey.setCellValueFactory(new PropertyValueFactory<>("licenseKey"));
        colType.setCellValueFactory(new PropertyValueFactory<>("licenseType"));

        colSeats.setCellValueFactory(cellData -> {
            LicenseModel l = cellData.getValue();
            int avail = l.getAvailableSeats() != null ? l.getAvailableSeats() : 0;
            int total = l.getTotalSeats() != null ? l.getTotalSeats() : 0;
            return javafx.beans.binding.Bindings.createStringBinding(() -> avail + " / " + total);
        });

        colExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    getStyleClass().removeAll("badge-active", "badge-expiring", "badge-expired", "badge-suspended");
                    LicenseModel rowLicense = getTableView().getItems().get(getIndex());
                    LocalDate today = LocalDate.now();

                    if (rowLicense != null && rowLicense.getExpiryDate() != null && rowLicense.getExpiryDate().isBefore(today)) {
                        setText("EXPIRED");
                        getStyleClass().add("badge-expired");
                    } else if ("ACTIVE".equalsIgnoreCase(item)) {
                        getStyleClass().add("badge-active");
                    } else if ("EXPIRING".equalsIgnoreCase(item)) {
                        getStyleClass().add("badge-expiring");
                    } else {
                        getStyleClass().add("badge-suspended");
                    }
                }
            }
        });

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("btn-secondary");
                editBtn.setStyle("-fx-padding: 3 8 3 8; -fx-font-size: 11px;");
                deleteBtn.getStyleClass().add("btn-danger");
                deleteBtn.setStyle("-fx-padding: 3 8 3 8; -fx-font-size: 11px;");

                editBtn.setOnAction(event -> {
                    LicenseModel license = getTableView().getItems().get(getIndex());
                    handleEdit(license);
                });

                deleteBtn.setOnAction(event -> {
                    LicenseModel license = getTableView().getItems().get(getIndex());
                    handleDelete(license);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void setupSearchAndFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "EXPIRING", "EXPIRED", "SUSPENDED"));
        filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        softwareFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        expiringCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        licenseTable.setItems(filteredData);
    }

    private void applyFilters() {
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String selectedStatus = statusFilter.getValue();
        SoftwareModel selectedSoftware = softwareFilter.getValue();
        boolean filterExpiringOnly = expiringCheckBox.isSelected();

        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);

        filteredData.setPredicate(l -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    (l.getLicenseKey() != null && l.getLicenseKey().toLowerCase().contains(searchText)) ||
                    (l.getSoftwareName() != null && l.getSoftwareName().toLowerCase().contains(searchText)) ||
                    (l.getVendorName() != null && l.getVendorName().toLowerCase().contains(searchText));

            boolean matchesStatus = selectedStatus == null || "ALL".equalsIgnoreCase(selectedStatus) ||
                    (l.getStatus() != null && l.getStatus().equalsIgnoreCase(selectedStatus));

            boolean matchesSoftware = selectedSoftware == null ||
                    (l.getSoftwareId() != null && l.getSoftwareId().equals(selectedSoftware.getSoftwareId()));

            boolean matchesExpiring = !filterExpiringOnly ||
                    (l.getExpiryDate() != null && !l.getExpiryDate().isBefore(today) && !l.getExpiryDate().isAfter(in30Days));

            return matchesSearch && matchesStatus && matchesSoftware && matchesExpiring;
        });
    }

    private void loadSoftwareList() {
        softwareApiClient.getAllSoftware().thenAccept(list -> {
            Platform.runLater(() -> {
                this.softwareList = list;
                if (list != null) {
                    softwareFilter.setItems(FXCollections.observableArrayList(list));
                }
            });
        });
    }

    @FXML
    public void loadData() {
        apiClient.getAllLicenses().thenAccept(list -> {
            Platform.runLater(() -> {
                if (list != null) {
                    masterData.setAll(list);
                }
            });
        });
    }

    @FXML
    public void handleAdd() {
        showDialog(null);
    }

    private void handleEdit(LicenseModel license) {
        showDialog(license);
    }

    private void handleDelete(LicenseModel license) {
        boolean confirm = AlertUtils.showConfirmation("Delete License", "Are you sure you want to delete license key '" + license.getLicenseKey() + "'?");
        if (confirm) {
            apiClient.deleteLicense(license.getLicenseId()).thenRun(() -> {
                AlertUtils.showInfo("Success", "License deleted successfully.");
                loadData();
            });
        }
    }

    private void showDialog(LicenseModel existing) {
        Dialog<LicenseModel> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add License" : "Edit License");
        dialog.setHeaderText(existing == null ? "Create New Software License Key" : "Update License Specifications");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<SoftwareModel> swCombo = new ComboBox<>();
        if (softwareList != null) {
            swCombo.setItems(FXCollections.observableArrayList(softwareList));
            if (existing != null && existing.getSoftwareId() != null) {
                softwareList.stream()
                        .filter(s -> s.getSoftwareId().equals(existing.getSoftwareId()))
                        .findFirst()
                        .ifPresent(swCombo::setValue);
            }
        }

        TextField keyField = new TextField(existing != null ? existing.getLicenseKey() : "");
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("Subscription", "Perpetual", "OEM", "Concurrent"));
        typeCombo.setValue(existing != null ? existing.getLicenseType() : "Subscription");

        DatePicker purchasePicker = new DatePicker(existing != null ? existing.getPurchaseDate() : LocalDate.now());
        DatePicker startPicker = new DatePicker(existing != null ? existing.getStartDate() : LocalDate.now());
        DatePicker expiryPicker = new DatePicker(existing != null ? existing.getExpiryDate() : LocalDate.now().plusYears(1));

        Spinner<Integer> totalSeatsSpinner = new Spinner<>(1, 1000, existing != null && existing.getTotalSeats() != null ? existing.getTotalSeats() : 10);
        Spinner<Integer> availSeatsSpinner = new Spinner<>(0, 1000, existing != null && existing.getAvailableSeats() != null ? existing.getAvailableSeats() : 10);

        TextField costField = new TextField(existing != null && existing.getCost() != null ? existing.getCost().toString() : "499.00");
        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("ACTIVE", "EXPIRED", "SUSPENDED"));
        statusCombo.setValue(existing != null && existing.getStatus() != null ? existing.getStatus() : "ACTIVE");

        grid.add(new Label("Software:"), 0, 0);
        grid.add(swCombo, 1, 0);
        grid.add(new Label("License Key:"), 0, 1);
        grid.add(keyField, 1, 1);
        grid.add(new Label("License Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("Purchase Date:"), 0, 3);
        grid.add(purchasePicker, 1, 3);
        grid.add(new Label("Start Date:"), 0, 4);
        grid.add(startPicker, 1, 4);
        grid.add(new Label("Expiry Date:"), 0, 5);
        grid.add(expiryPicker, 1, 5);
        grid.add(new Label("Total Seats:"), 0, 6);
        grid.add(totalSeatsSpinner, 1, 6);
        grid.add(new Label("Available Seats:"), 0, 7);
        grid.add(availSeatsSpinner, 1, 7);
        grid.add(new Label("Cost ($):"), 0, 8);
        grid.add(costField, 1, 8);
        grid.add(new Label("Status:"), 0, 9);
        grid.add(statusCombo, 1, 9);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                LicenseModel model = existing != null ? existing : new LicenseModel();
                if (swCombo.getValue() != null) {
                    model.setSoftwareId(swCombo.getValue().getSoftwareId());
                }
                model.setLicenseKey(keyField.getText().trim());
                model.setLicenseType(typeCombo.getValue());
                model.setPurchaseDate(purchasePicker.getValue());
                model.setStartDate(startPicker.getValue());
                model.setExpiryDate(expiryPicker.getValue());
                model.setTotalSeats(totalSeatsSpinner.getValue());
                model.setAvailableSeats(availSeatsSpinner.getValue());
                try {
                    model.setCost(new BigDecimal(costField.getText().trim()));
                } catch (Exception e) {
                    model.setCost(BigDecimal.ZERO);
                }
                model.setStatus(statusCombo.getValue());
                return model;
            }
            return null;
        });

        Optional<LicenseModel> result = dialog.showAndWait();
        result.ifPresent(lic -> {
            if (existing == null) {
                apiClient.createLicense(lic).thenRun(() -> {
                    AlertUtils.showInfo("Success", "License created successfully.");
                    loadData();
                });
            } else {
                apiClient.updateLicense(lic.getLicenseId(), lic).thenRun(() -> {
                    AlertUtils.showInfo("Success", "License updated successfully.");
                    loadData();
                });
            }
        });
    }
}
