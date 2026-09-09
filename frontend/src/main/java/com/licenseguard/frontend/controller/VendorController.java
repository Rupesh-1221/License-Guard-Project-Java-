package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.api.VendorApiClient;
import com.licenseguard.frontend.model.VendorModel;
import com.licenseguard.frontend.util.AlertUtils;
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

import java.util.Optional;

public class VendorController {

    @FXML private TextField searchField;
    @FXML private TableView<VendorModel> vendorTable;
    @FXML private TableColumn<VendorModel, Integer> colId;
    @FXML private TableColumn<VendorModel, String> colName;
    @FXML private TableColumn<VendorModel, String> colEmail;
    @FXML private TableColumn<VendorModel, String> colWebsite;
    @FXML private TableColumn<VendorModel, Integer> colSoftwareCount;
    @FXML private TableColumn<VendorModel, Void> colActions;

    private final VendorApiClient apiClient = new VendorApiClient();
    private final ObservableList<VendorModel> masterData = FXCollections.observableArrayList();
    private FilteredList<VendorModel> filteredData;

    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("vendorId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        colWebsite.setCellValueFactory(new PropertyValueFactory<>("website"));
        colSoftwareCount.setCellValueFactory(new PropertyValueFactory<>("softwareCount"));

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
                    VendorModel vendor = getTableView().getItems().get(getIndex());
                    handleEdit(vendor);
                });

                deleteBtn.setOnAction(event -> {
                    VendorModel vendor = getTableView().getItems().get(getIndex());
                    handleDelete(vendor);
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

    private void setupSearch() {
        filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(vendor -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }
                String filter = newValue.toLowerCase();
                if (vendor.getVendorName() != null && vendor.getVendorName().toLowerCase().contains(filter)) {
                    return true;
                }
                if (vendor.getContactEmail() != null && vendor.getContactEmail().toLowerCase().contains(filter)) {
                    return true;
                }
                return vendor.getWebsite() != null && vendor.getWebsite().toLowerCase().contains(filter);
            });
        });
        vendorTable.setItems(filteredData);
    }

    @FXML
    public void loadData() {
        apiClient.getAllVendors().thenAccept(list -> {
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

    private void handleEdit(VendorModel vendor) {
        showDialog(vendor);
    }

    private void handleDelete(VendorModel vendor) {
        boolean confirm = AlertUtils.showConfirmation("Delete Vendor", "Are you sure you want to delete vendor '" + vendor.getVendorName() + "'?");
        if (confirm) {
            apiClient.deleteVendor(vendor.getVendorId()).thenRun(() -> {
                AlertUtils.showInfo("Success", "Vendor deleted successfully.");
                loadData();
            });
        }
    }

    private void showDialog(VendorModel existing) {
        Dialog<VendorModel> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Vendor" : "Edit Vendor");
        dialog.setHeaderText(existing == null ? "Register New Software Vendor" : "Update Vendor Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(existing != null ? existing.getVendorName() : "");
        nameField.setPromptText("Vendor Name");
        TextField emailField = new TextField(existing != null ? existing.getContactEmail() : "");
        emailField.setPromptText("Contact Email");
        TextField websiteField = new TextField(existing != null ? existing.getWebsite() : "");
        websiteField.setPromptText("Website URL");

        grid.add(new Label("Vendor Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Contact Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Website:"), 0, 2);
        grid.add(websiteField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                VendorModel model = existing != null ? existing : new VendorModel();
                model.setVendorName(nameField.getText().trim());
                model.setContactEmail(emailField.getText().trim());
                model.setWebsite(websiteField.getText().trim());
                return model;
            }
            return null;
        });

        Optional<VendorModel> result = dialog.showAndWait();
        result.ifPresent(vendor -> {
            if (existing == null) {
                apiClient.createVendor(vendor).thenRun(() -> {
                    AlertUtils.showInfo("Success", "Vendor created successfully.");
                    loadData();
                });
            } else {
                apiClient.updateVendor(vendor.getVendorId(), vendor).thenRun(() -> {
                    AlertUtils.showInfo("Success", "Vendor updated successfully.");
                    loadData();
                });
            }
        });
    }
}
