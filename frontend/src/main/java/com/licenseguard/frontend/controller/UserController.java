package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.api.DepartmentApiClient;
import com.licenseguard.frontend.api.UserApiClient;
import com.licenseguard.frontend.model.DepartmentModel;
import com.licenseguard.frontend.model.UserModel;
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

public class UserController {

    @FXML private TextField searchField;
    @FXML private ComboBox<DepartmentModel> departmentFilter;
    @FXML private TableView<UserModel> userTable;
    @FXML private TableColumn<UserModel, Integer> colId;
    @FXML private TableColumn<UserModel, String> colName;
    @FXML private TableColumn<UserModel, String> colEmail;
    @FXML private TableColumn<UserModel, String> colRole;
    @FXML private TableColumn<UserModel, String> colDepartment;
    @FXML private TableColumn<UserModel, String> colStatus;
    @FXML private TableColumn<UserModel, Void> colActions;

    private final UserApiClient apiClient = new UserApiClient();
    private final DepartmentApiClient departmentApiClient = new DepartmentApiClient();
    private final ObservableList<UserModel> masterData = FXCollections.observableArrayList();
    private FilteredList<UserModel> filteredData;
    private List<DepartmentModel> departmentList;

    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
        loadDepartments();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
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
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("btn-secondary");
                editBtn.setStyle("-fx-padding: 3 8 3 8; -fx-font-size: 11px;");
                deleteBtn.getStyleClass().add("btn-danger");
                deleteBtn.setStyle("-fx-padding: 3 8 3 8; -fx-font-size: 11px;");

                editBtn.setOnAction(event -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    handleEdit(user);
                });

                deleteBtn.setOnAction(event -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    handleDelete(user);
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
        departmentFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        userTable.setItems(filteredData);
    }

    private void applyFilters() {
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        DepartmentModel selectedDept = departmentFilter.getValue();

        filteredData.setPredicate(user -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    (user.getName() != null && user.getName().toLowerCase().contains(searchText)) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText)) ||
                    (user.getRole() != null && user.getRole().toLowerCase().contains(searchText));

            boolean matchesDept = selectedDept == null ||
                    (user.getDepartmentId() != null && user.getDepartmentId().equals(selectedDept.getDepartmentId()));

            return matchesSearch && matchesDept;
        });
    }

    private void loadDepartments() {
        departmentApiClient.getAllDepartments().thenAccept(depts -> {
            Platform.runLater(() -> {
                this.departmentList = depts;
                if (depts != null) {
                    departmentFilter.setItems(FXCollections.observableArrayList(depts));
                }
            });
        });
    }

    @FXML
    public void loadData() {
        apiClient.getAllUsers().thenAccept(list -> {
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

    private void handleEdit(UserModel user) {
        showDialog(user);
    }

    private void handleDelete(UserModel user) {
        boolean confirm = AlertUtils.showConfirmation("Delete User", "Are you sure you want to delete user '" + user.getName() + "'?");
        if (confirm) {
            apiClient.deleteUser(user.getUserId()).thenRun(() -> {
                AlertUtils.showInfo("Success", "User deleted successfully.");
                loadData();
            });
        }
    }

    private void showDialog(UserModel existing) {
        Dialog<UserModel> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add User" : "Edit User");
        dialog.setHeaderText(existing == null ? "Register New User" : "Update User Profile");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(existing != null ? existing.getName() : "");
        TextField emailField = new TextField(existing != null ? existing.getEmail() : "");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(existing == null ? "Password" : "Leave blank to keep current");

        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList("ADMIN", "USER", "MANAGER"));
        roleCombo.setValue(existing != null ? existing.getRole() : "USER");

        ComboBox<DepartmentModel> deptCombo = new ComboBox<>();
        if (departmentList != null) {
            deptCombo.setItems(FXCollections.observableArrayList(departmentList));
            if (existing != null && existing.getDepartmentId() != null) {
                departmentList.stream()
                        .filter(d -> d.getDepartmentId().equals(existing.getDepartmentId()))
                        .findFirst()
                        .ifPresent(deptCombo::setValue);
            }
        }

        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        statusCombo.setValue(existing != null && existing.getStatus() != null ? existing.getStatus() : "ACTIVE");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);
        grid.add(new Label("Department:"), 0, 4);
        grid.add(deptCombo, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                UserModel model = existing != null ? existing : new UserModel();
                model.setName(nameField.getText().trim());
                model.setEmail(emailField.getText().trim());
                if (!passwordField.getText().isBlank()) {
                    model.setPassword(passwordField.getText().trim());
                }
                model.setRole(roleCombo.getValue());
                model.setStatus(statusCombo.getValue());
                if (deptCombo.getValue() != null) {
                    model.setDepartmentId(deptCombo.getValue().getDepartmentId());
                }
                return model;
            }
            return null;
        });

        Optional<UserModel> result = dialog.showAndWait();
        result.ifPresent(user -> {
            if (existing == null) {
                apiClient.createUser(user).thenRun(() -> {
                    AlertUtils.showInfo("Success", "User created successfully.");
                    loadData();
                });
            } else {
                apiClient.updateUser(user.getUserId(), user).thenRun(() -> {
                    AlertUtils.showInfo("Success", "User updated successfully.");
                    loadData();
                });
            }
        });
    }
}
