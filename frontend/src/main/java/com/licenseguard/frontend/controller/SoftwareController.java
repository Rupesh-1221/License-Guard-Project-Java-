package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.api.SoftwareApiClient;
import com.licenseguard.frontend.api.VendorApiClient;
import com.licenseguard.frontend.model.SoftwareModel;
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

import java.util.List;
import java.util.Optional;

public class SoftwareController {

    @FXML private TextField searchField;
    @FXML private ComboBox<VendorModel> vendorFilter;
    @FXML private TableView<SoftwareModel> softwareTable;
    @FXML private TableColumn<SoftwareModel, Integer> colId;
    @FXML private TableColumn<SoftwareModel, String> colName;
    @FXML private TableColumn<SoftwareModel, String> colVersion;
    @FXML private TableColumn<SoftwareModel, String> colVendor;
    @FXML private TableColumn<SoftwareModel, String> colDescription;
    @FXML private TableColumn<SoftwareModel, Integer> colLicenseCount;
    @FXML private TableColumn<SoftwareModel, Void> colActions;

    private final SoftwareApiClient apiClient = new SoftwareApiClient();
    private final VendorApiClient vendorApiClient = new VendorApiClient();
    private final ObservableList<SoftwareModel> masterData = FXCollections.observableArrayList();
    private FilteredList<SoftwareModel> filteredData;
    private List<VendorModel> vendorList;

    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
        loadVendors();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("softwareId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("softwareName"));
        colVersion.setCellValueFactory(new PropertyValueFactory<>("version"));
        colVendor.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colLicenseCount.setCellValueFactory(new PropertyValueFactory<>("licenseCount"));

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
                    SoftwareModel software = getTableView().getItems().get(getIndex());
                    handleEdit(software);
                });

                deleteBtn.setOnAction(event -> {
                    SoftwareModel software = getTableView().getItems().get(getIndex());
                    handleDelete(software);
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
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        vendorFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        softwareTable.setItems(filteredData);
    }

    private void applyFilters() {
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        VendorModel selectedVendor = vendorFilter.getValue();

        filteredData.setPredicate(s -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    (s.getSoftwareName() != null && s.getSoftwareName().toLowerCase().contains(searchText)) ||
                    (s.getVersion() != null && s.getVersion().toLowerCase().contains(searchText)) ||
                    (s.getDescription() != null && s.getDescription().toLowerCase().contains(searchText));

            boolean matchesVendor = selectedVendor == null ||
                    (s.getVendorId() != null && s.getVendorId().equals(selectedVendor.getVendorId()));

            return matchesSearch && matchesVendor;
        });
    }

    private void loadVendors() {
        vendorApiClient.getAllVendors().thenAccept(vendors -> {
            Platform.runLater(() -> {
                this.vendorList = vendors;
                if (vendors != null) {
                    vendorFilter.setItems(FXCollections.observableArrayList(vendors));
                }
            });
        });
    }

    @FXML
    public void loadData() {
        apiClient.getAllSoftware().thenAccept(list -> {
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

    private void handleEdit(SoftwareModel software) {
        showDialog(software);
    }

    private void handleDelete(SoftwareModel software) {
        boolean confirm = AlertUtils.showConfirmation("Delete Software", "Are you sure you want to delete software '" + software.getSoftwareName() + "'?");
        if (confirm) {
            apiClient.deleteSoftware(software.getSoftwareId()).thenRun(() -> {
                AlertUtils.showInfo("Success", "Software deleted successfully.");
                loadData();
            });
        }
    }

    private void showDialog(SoftwareModel existing) {
        Dialog<SoftwareModel> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Software" : "Edit Software");
        dialog.setHeaderText(existing == null ? "Register New Software Entry" : "Update Software Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(existing != null ? existing.getSoftwareName() : "");
        nameField.setPromptText("Software Name");
        TextField versionField = new TextField(existing != null ? existing.getVersion() : "");
        versionField.setPromptText("Version (e.g. 2.1.0)");
        TextArea descField = new TextArea(existing != null ? existing.getDescription() : "");
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);

        ComboBox<VendorModel> vendorCombo = new ComboBox<>();
        if (vendorList != null) {
            vendorCombo.setItems(FXCollections.observableArrayList(vendorList));
            if (existing != null && existing.getVendorId() != null) {
                vendorList.stream()
                        .filter(v -> v.getVendorId().equals(existing.getVendorId()))
                        .findFirst()
                        .ifPresent(vendorCombo::setValue);
            }
        }

        grid.add(new Label("Software Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Version:"), 0, 1);
        grid.add(versionField, 1, 1);
        grid.add(new Label("Vendor:"), 0, 2);
        grid.add(vendorCombo, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                SoftwareModel model = existing != null ? existing : new SoftwareModel();
                model.setSoftwareName(nameField.getText().trim());
                model.setVersion(versionField.getText().trim());
                model.setDescription(descField.getText().trim());
                if (vendorCombo.getValue() != null) {
                    model.setVendorId(vendorCombo.getValue().getVendorId());
                }
                return model;
            }
            return null;
        });

        Optional<SoftwareModel> result = dialog.showAndWait();
        result.ifPresent(software -> {
            if (existing == null) {
                apiClient.createSoftware(software).thenRun(() -> {
                    AlertUtils.showInfo("Success", "Software created successfully.");
                    loadData();
                });
            } else {
                apiClient.updateSoftware(software.getSoftwareId(), software).thenRun(() -> {
                    AlertUtils.showInfo("Success", "Software updated successfully.");
                    loadData();
                });
            }
        });
    }
}
