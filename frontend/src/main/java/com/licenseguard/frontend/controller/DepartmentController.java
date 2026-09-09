package com.licenseguard.frontend.controller;

import com.licenseguard.frontend.api.DepartmentApiClient;
import com.licenseguard.frontend.model.DepartmentModel;
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

public class DepartmentController {

    @FXML private TextField searchField;
    @FXML private TableView<DepartmentModel> departmentTable;
    @FXML private TableColumn<DepartmentModel, Integer> colId;
    @FXML private TableColumn<DepartmentModel, String> colName;
    @FXML private TableColumn<DepartmentModel, String> colDescription;
    @FXML private TableColumn<DepartmentModel, Integer> colUserCount;
    @FXML private TableColumn<DepartmentModel, Void> colActions;

    private final DepartmentApiClient apiClient = new DepartmentApiClient();
    private final ObservableList<DepartmentModel> masterData = FXCollections.observableArrayList();
    private FilteredList<DepartmentModel> filteredData;

    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUserCount.setCellValueFactory(new PropertyValueFactory<>("userCount"));

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
                    DepartmentModel dept = getTableView().getItems().get(getIndex());
                    handleEdit(dept);
                });

                deleteBtn.setOnAction(event -> {
                    DepartmentModel dept = getTableView().getItems().get(getIndex());
                    handleDelete(dept);
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
            filteredData.setPredicate(department -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }
                String filter = newValue.toLowerCase();
                if (department.getDepartmentName() != null && department.getDepartmentName().toLowerCase().contains(filter)) {
                    return true;
                }
                return department.getDescription() != null && department.getDescription().toLowerCase().contains(filter);
            });
        });
        departmentTable.setItems(filteredData);
    }

    @FXML
    public void loadData() {
        apiClient.getAllDepartments().thenAccept(list -> {
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

    private void handleEdit(DepartmentModel department) {
        showDialog(department);
    }

    private void handleDelete(DepartmentModel department) {
        boolean confirm = AlertUtils.showConfirmation("Delete Department",
                "Are you sure you want to delete department '" + department.getDepartmentName() + "'?");
        if (confirm) {
            apiClient.deleteDepartment(department.getDepartmentId()).thenRun(() -> {
                AlertUtils.showInfo("Success", "Department deleted successfully.");
                loadData();
            });
        }
    }

    private void showDialog(DepartmentModel existing) {
        Dialog<DepartmentModel> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Department" : "Edit Department");
        dialog.setHeaderText(existing == null ? "Create New Department" : "Update Department Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(existing != null ? existing.getDepartmentName() : "");
        nameField.setPromptText("Department Name");
        TextArea descField = new TextArea(existing != null ? existing.getDescription() : "");
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                DepartmentModel model = existing != null ? existing : new DepartmentModel();
                model.setDepartmentName(nameField.getText().trim());
                model.setDescription(descField.getText().trim());
                return model;
            }
            return null;
        });

        Optional<DepartmentModel> result = dialog.showAndWait();
        result.ifPresent(dept -> {
            if (existing == null) {
                apiClient.createDepartment(dept).thenRun(() -> {
                    AlertUtils.showInfo("Success", "Department created successfully.");
                    loadData();
                });
            } else {
                apiClient.updateDepartment(dept.getDepartmentId(), dept).thenRun(() -> {
                    AlertUtils.showInfo("Success", "Department updated successfully.");
                    loadData();
                });
            }
        });
    }
}
