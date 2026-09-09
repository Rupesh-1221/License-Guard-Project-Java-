package com.licenseguard.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.licenseguard.frontend.model.ApiResponseModel;
import com.licenseguard.frontend.model.DepartmentModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DepartmentApiClient {
    private final ApiClient apiClient = ApiClient.getInstance();

    public CompletableFuture<List<DepartmentModel>> getAllDepartments() {
        return apiClient.get("/api/departments", new TypeReference<ApiResponseModel<List<DepartmentModel>>>() {});
    }

    public CompletableFuture<DepartmentModel> getDepartmentById(Integer id) {
        return apiClient.get("/api/departments/" + id, new TypeReference<ApiResponseModel<DepartmentModel>>() {});
    }

    public CompletableFuture<DepartmentModel> createDepartment(DepartmentModel department) {
        return apiClient.post("/api/departments", department, new TypeReference<ApiResponseModel<DepartmentModel>>() {});
    }

    public CompletableFuture<DepartmentModel> updateDepartment(Integer id, DepartmentModel department) {
        return apiClient.put("/api/departments/" + id, department, new TypeReference<ApiResponseModel<DepartmentModel>>() {});
    }

    public CompletableFuture<Void> deleteDepartment(Integer id) {
        return apiClient.delete("/api/departments/" + id, new TypeReference<ApiResponseModel<Void>>() {});
    }
}
