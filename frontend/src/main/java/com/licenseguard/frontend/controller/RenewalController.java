package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.api.LicenseApiClient;
import com.licenseguard.frontend.api.RenewalApiClient;
import com.licenseguard.frontend.model.LicenseModel;
import com.licenseguard.frontend.model.RenewalModel;
import com.licenseguard.frontend.util.AlertUtils;
import com.licenseguard.frontend.util.DateFormatter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RenewalController {

    @FXML private ComboBox<LicenseModel> licenseComboBox;
    @FXML private TextField currentExpiryField;
    @FXML private DatePicker newExpiryPicker;
    @FXML private TextField costField;
    @FXML private TextField remarksField;

    @FXML private TextField searchField;
    @FXML private TableView<RenewalModel> renewalTable;
    @FXML private TableColumn<RenewalModel, Integer> colId;
    @FXML private TableColumn<RenewalModel, String> colSoftware;
    @FXML private TableColumn<RenewalModel, String> colLicenseKey;
    @FXML private TableColumn<RenewalModel, LocalDate> colOldExpiry;
    @FXML private TableColumn<RenewalModel, LocalDate> colNewExpiry;
    @FXML private TableColumn<RenewalModel, LocalDate> colRenewalDate;
    @FXML private TableColumn<RenewalModel, BigDecimal> colCost;
    @FXML private TableColumn<RenewalModel, String> colRenewedBy;
    @FXML private TableColumn<RenewalModel, String> colRemarks;

    private final RenewalApiClient renewalApiClient = new RenewalApiClient();
    private final LicenseApiClient licenseApiClient = new LicenseApiClient();

    private final ObservableList<RenewalModel> masterData = FXCollections.observableArrayList();
    private FilteredList<RenewalModel> filteredData;

    @FXML
    public void initialize() {
        setupTable();
        setupFormListeners();
        setupSearch();
        loadLicenses();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("renewalId"));
        colSoftware.setCellValueFactory(new PropertyValueFactory<>("softwareName"));
        colLicenseKey.setCellValueFactory(new PropertyValueFactory<>("licenseKey"));
        colOldExpiry.setCellValueFactory(new PropertyValueFactory<>("oldExpiryDate"));
        colNewExpiry.setCellValueFactory(new PropertyValueFactory<>("newExpiryDate"));
        colRenewalDate.setCellValueFactory(new PropertyValueFactory<>("renewalDate"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("renewalCost"));
        colRenewedBy.setCellValueFactory(new PropertyValueFactory<>("renewedByUserName"));
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
    }

    private void setupFormListeners() {
        licenseComboBox.valueProperty().addListener((obs, oldVal, selectedLicense) -> {
            if (selectedLicense != null && selectedLicense.getExpiryDate() != null) {
                currentExpiryField.setText(DateFormatter.format(selectedLicense.getExpiryDate()));
                newExpiryPicker.setValue(selectedLicense.getExpiryDate().plusYears(1));
            } else {
                currentExpiryField.setText("N/A");
                newExpiryPicker.setValue(null);
            }
        });
    }

    private void setupSearch() {
        filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal != null ? newVal.toLowerCase().trim() : "";
            filteredData.setPredicate(r -> {
                if (filter.isEmpty()) return true;
                return (r.getSoftwareName() != null && r.getSoftwareName().toLowerCase().contains(filter)) ||
                        (r.getLicenseKey() != null && r.getLicenseKey().toLowerCase().contains(filter)) ||
                        (r.getRenewedByUserName() != null && r.getRenewedByUserName().toLowerCase().contains(filter)) ||
                        (r.getRemarks() != null && r.getRemarks().toLowerCase().contains(filter));
            });
        });
        renewalTable.setItems(filteredData);
    }

    private void loadLicenses() {
        licenseApiClient.getAllLicenses().thenAccept(licenses -> {
            Platform.runLater(() -> {
                if (licenses != null) {
                    licenseComboBox.setItems(FXCollections.observableArrayList(licenses));
                }
            });
        });
    }

    @FXML
    public void loadData() {
        renewalApiClient.getAllRenewals().thenAccept(list -> {
            Platform.runLater(() -> {
                if (list != null) {
                    masterData.setAll(list);
                }
            });
        });
    }

    @FXML
    public void handleRenew() {
        LicenseModel selectedLicense = licenseComboBox.getValue();
        LocalDate newExpiry = newExpiryPicker.getValue();
        String costStr = costField.getText() != null ? costField.getText().trim() : "";
        String remarks = remarksField.getText() != null ? remarksField.getText().trim() : "";

        if (selectedLicense == null) {
            AlertUtils.showWarning("Form Warning", "Please select a license to renew.");
            return;
        }

        if (newExpiry == null) {
            AlertUtils.showWarning("Form Warning", "Please select a valid new expiry date.");
            return;
        }

        LocalDate currentExpiry = selectedLicense.getExpiryDate();
        if (currentExpiry != null && !newExpiry.isAfter(currentExpiry)) {
            AlertUtils.showError("Validation Error", "New expiry date (" + newExpiry + ") must be after current expiry date (" + currentExpiry + ").");
            return;
        }

        BigDecimal cost = BigDecimal.ZERO;
        if (!costStr.isEmpty()) {
            try {
                cost = new BigDecimal(costStr);
            } catch (Exception e) {
                AlertUtils.showError("Invalid Input", "Cost must be a valid number (e.g. 299.99).");
                return;
            }
        }

        RenewalModel request = new RenewalModel();
        request.setLicenseId(selectedLicense.getLicenseId());
        request.setNewExpiryDate(newExpiry);
        request.setRenewalDate(LocalDate.now());
        request.setRenewalCost(cost);
        request.setRemarks(remarks);

        renewalApiClient.renewLicense(request).thenRun(() -> {
            Platform.runLater(() -> {
                AlertUtils.showInfo("Success", "License key '" + selectedLicense.getLicenseKey() + "' renewed successfully until " + newExpiry + "!");
                licenseComboBox.setValue(null);
                currentExpiryField.clear();
                newExpiryPicker.setValue(null);
                costField.clear();
                remarksField.clear();
                loadLicenses();
                loadData();
            });
        });
    }
}
