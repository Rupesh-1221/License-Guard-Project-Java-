package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.api.AssignmentApiClient;
import com.licenseguard.frontend.api.LicenseApiClient;
import com.licenseguard.frontend.api.UserApiClient;
import com.licenseguard.frontend.model.AssignmentModel;
import com.licenseguard.frontend.model.LicenseModel;
import com.licenseguard.frontend.model.UserModel;
import com.licenseguard.frontend.util.AlertUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class AssignmentController {

    @FXML private ComboBox<UserModel> userComboBox;
    @FXML private ComboBox<LicenseModel> licenseComboBox;
    @FXML private Label seatBadge;
    @FXML private Button btnAssign;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    @FXML private TableView<AssignmentModel> assignmentTable;
    @FXML private TableColumn<AssignmentModel, Integer> colId;
    @FXML private TableColumn<AssignmentModel, String> colUser;
    @FXML private TableColumn<AssignmentModel, String> colUserEmail;
    @FXML private TableColumn<AssignmentModel, String> colSoftware;
    @FXML private TableColumn<AssignmentModel, String> colLicenseKey;
    @FXML private TableColumn<AssignmentModel, LocalDate> colAssignedDate;
    @FXML private TableColumn<AssignmentModel, LocalDate> colUnassignedDate;
    @FXML private TableColumn<AssignmentModel, String> colStatus;
    @FXML private TableColumn<AssignmentModel, Void> colActions;

    private final AssignmentApiClient assignmentApiClient = new AssignmentApiClient();
    private final LicenseApiClient licenseApiClient = new LicenseApiClient();
    private final UserApiClient userApiClient = new UserApiClient();

    private final ObservableList<AssignmentModel> masterData = FXCollections.observableArrayList();
    private FilteredList<AssignmentModel> filteredData;

    @FXML
    public void initialize() {
        setupTable();
        setupFormListeners();
        setupSearchAndFilters();
        loadFormData();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("assignmentId"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        colSoftware.setCellValueFactory(new PropertyValueFactory<>("softwareName"));
        colLicenseKey.setCellValueFactory(new PropertyValueFactory<>("licenseKey"));
        colAssignedDate.setCellValueFactory(new PropertyValueFactory<>("assignedDate"));
        colUnassignedDate.setCellValueFactory(new PropertyValueFactory<>("unassignedDate"));
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
                    getStyleClass().removeAll("badge-active", "badge-suspended");
                    if ("ACTIVE".equalsIgnoreCase(item)) {
                        getStyleClass().add("badge-active");
                    } else {
                        getStyleClass().add("badge-suspended");
                    }
                }
            }
        });

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button unassignBtn = new Button("Unassign");

            {
                unassignBtn.getStyleClass().add("btn-warning");
                unassignBtn.setStyle("-fx-padding: 3 8 3 8; -fx-font-size: 11px;");
                unassignBtn.setOnAction(event -> {
                    AssignmentModel model = getTableView().getItems().get(getIndex());
                    handleUnassign(model);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AssignmentModel model = getTableView().getItems().get(getIndex());
                    if (model != null && "ACTIVE".equalsIgnoreCase(model.getStatus())) {
                        setGraphic(unassignBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void setupFormListeners() {
        btnAssign.setDisable(true);

        licenseComboBox.valueProperty().addListener((obs, oldVal, selectedLicense) -> {
            updateSeatBadge(selectedLicense);
        });
    }

    private void updateSeatBadge(LicenseModel selectedLicense) {
        if (selectedLicense == null) {
            seatBadge.setText("Select a license");
            seatBadge.getStyleClass().removeAll("badge-active", "badge-expired");
            seatBadge.getStyleClass().add("badge-suspended");
            btnAssign.setDisable(true);
            return;
        }

        int avail = selectedLicense.getAvailableSeats() != null ? selectedLicense.getAvailableSeats() : 0;
        int total = selectedLicense.getTotalSeats() != null ? selectedLicense.getTotalSeats() : 0;

        seatBadge.getStyleClass().removeAll("badge-active", "badge-expired", "badge-suspended");

        if (avail <= 0) {
            seatBadge.setText("0 / " + total + " Seats (UNAVAILABLE)");
            seatBadge.getStyleClass().add("badge-expired");
            btnAssign.setDisable(true);
        } else {
            seatBadge.setText(avail + " / " + total + " Seats Available");
            seatBadge.getStyleClass().add("badge-active");
            btnAssign.setDisable(userComboBox.getValue() == null);
        }
    }

    private void setupSearchAndFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        userComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (licenseComboBox.getValue() != null) {
                updateSeatBadge(licenseComboBox.getValue());
            }
        });

        assignmentTable.setItems(filteredData);
    }

    private void applyFilters() {
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String selectedStatus = statusFilter.getValue();

        filteredData.setPredicate(a -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    (a.getUserName() != null && a.getUserName().toLowerCase().contains(searchText)) ||
                    (a.getUserEmail() != null && a.getUserEmail().toLowerCase().contains(searchText)) ||
                    (a.getSoftwareName() != null && a.getSoftwareName().toLowerCase().contains(searchText)) ||
                    (a.getLicenseKey() != null && a.getLicenseKey().toLowerCase().contains(searchText));

            boolean matchesStatus = selectedStatus == null || "ALL".equalsIgnoreCase(selectedStatus) ||
                    (a.getStatus() != null && a.getStatus().equalsIgnoreCase(selectedStatus));

            return matchesSearch && matchesStatus;
        });
    }

    private void loadFormData() {
        userApiClient.getAllUsers().thenAccept(users -> {
            Platform.runLater(() -> {
                if (users != null) {
                    userComboBox.setItems(FXCollections.observableArrayList(users));
                }
            });
        });

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
        assignmentApiClient.getAllAssignments().thenAccept(list -> {
            Platform.runLater(() -> {
                if (list != null) {
                    masterData.setAll(list);
                }
            });
        });
    }

    @FXML
    public void handleAssign() {
        UserModel user = userComboBox.getValue();
        LicenseModel license = licenseComboBox.getValue();

        if (user == null || license == null) {
            AlertUtils.showWarning("Form Warning", "Please select both a user and a license.");
            return;
        }

        if (license.getAvailableSeats() != null && license.getAvailableSeats() <= 0) {
            AlertUtils.showError("Seat Exhausted", "No available seats left for license key: " + license.getLicenseKey());
            return;
        }

        AssignmentModel request = new AssignmentModel();
        request.setUserId(user.getUserId());
        request.setLicenseId(license.getLicenseId());
        request.setAssignedDate(LocalDate.now());

        assignmentApiClient.assignLicense(request).thenRun(() -> {
            Platform.runLater(() -> {
                AlertUtils.showInfo("Success", "License assigned to " + user.getName() + " successfully.");
                userComboBox.setValue(null);
                licenseComboBox.setValue(null);
                loadFormData();
                loadData();
            });
        });
    }

    private void handleUnassign(AssignmentModel assignment) {
        boolean confirm = AlertUtils.showConfirmation("Unassign License",
                "Are you sure you want to unassign license '" + assignment.getSoftwareName() + "' from user '" + assignment.getUserName() + "'?");
        if (confirm) {
            assignmentApiClient.unassignLicense(assignment.getAssignmentId()).thenRun(() -> {
                Platform.runLater(() -> {
                    AlertUtils.showInfo("Success", "License unassigned successfully.");
                    loadFormData();
                    loadData();
                });
            });
        }
    }
}
