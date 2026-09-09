package com.licenseguard.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.licenseguard.frontend.model.ApiResponseModel;
import com.licenseguard.frontend.model.AssignmentModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AssignmentApiClient {
    private final ApiClient apiClient = ApiClient.getInstance();

    public CompletableFuture<List<AssignmentModel>> getAllAssignments() {
        return apiClient.get("/api/license-assignments", new TypeReference<ApiResponseModel<List<AssignmentModel>>>() {});
    }

    public CompletableFuture<List<AssignmentModel>> getAssignmentsByLicense(Integer licenseId) {
        return apiClient.get("/api/license-assignments?licenseId=" + licenseId, new TypeReference<ApiResponseModel<List<AssignmentModel>>>() {});
    }

    public CompletableFuture<List<AssignmentModel>> getAssignmentsByUser(Integer userId) {
        return apiClient.get("/api/license-assignments?userId=" + userId, new TypeReference<ApiResponseModel<List<AssignmentModel>>>() {});
    }

    public CompletableFuture<AssignmentModel> getAssignmentById(Integer id) {
        return apiClient.get("/api/license-assignments/" + id, new TypeReference<ApiResponseModel<AssignmentModel>>() {});
    }

    public CompletableFuture<AssignmentModel> assignLicense(AssignmentModel assignment) {
        return apiClient.post("/api/license-assignments", assignment, new TypeReference<ApiResponseModel<AssignmentModel>>() {});
    }

    public CompletableFuture<AssignmentModel> unassignLicense(Integer assignmentId) {
        return apiClient.put("/api/license-assignments/" + assignmentId + "/unassign", null, new TypeReference<ApiResponseModel<AssignmentModel>>() {});
    }

    public CompletableFuture<Void> deleteAssignment(Integer id) {
        return apiClient.delete("/api/license-assignments/" + id, new TypeReference<ApiResponseModel<Void>>() {});
    }
}
