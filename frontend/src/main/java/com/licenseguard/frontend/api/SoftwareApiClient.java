package com.licenseguard.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.licenseguard.frontend.model.ApiResponseModel;
import com.licenseguard.frontend.model.SoftwareModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SoftwareApiClient {
    private final ApiClient apiClient = ApiClient.getInstance();

    public CompletableFuture<List<SoftwareModel>> getAllSoftware() {
        return apiClient.get("/api/software", new TypeReference<ApiResponseModel<List<SoftwareModel>>>() {});
    }

    public CompletableFuture<List<SoftwareModel>> getSoftwareByVendor(Integer vendorId) {
        return apiClient.get("/api/software?vendorId=" + vendorId, new TypeReference<ApiResponseModel<List<SoftwareModel>>>() {});
    }

    public CompletableFuture<SoftwareModel> getSoftwareById(Integer id) {
        return apiClient.get("/api/software/" + id, new TypeReference<ApiResponseModel<SoftwareModel>>() {});
    }

    public CompletableFuture<SoftwareModel> createSoftware(SoftwareModel software) {
        return apiClient.post("/api/software", software, new TypeReference<ApiResponseModel<SoftwareModel>>() {});
    }

    public CompletableFuture<SoftwareModel> updateSoftware(Integer id, SoftwareModel software) {
        return apiClient.put("/api/software/" + id, software, new TypeReference<ApiResponseModel<SoftwareModel>>() {});
    }

    public CompletableFuture<Void> deleteSoftware(Integer id) {
        return apiClient.delete("/api/software/" + id, new TypeReference<ApiResponseModel<Void>>() {});
    }
}
