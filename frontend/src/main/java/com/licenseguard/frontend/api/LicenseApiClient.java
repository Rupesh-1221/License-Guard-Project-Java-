package com.licenseguard.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.licenseguard.frontend.model.ApiResponseModel;
import com.licenseguard.frontend.model.LicenseModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LicenseApiClient {
    private final ApiClient apiClient = ApiClient.getInstance();

    public CompletableFuture<List<LicenseModel>> getAllLicenses() {
        return apiClient.get("/api/licenses", new TypeReference<ApiResponseModel<List<LicenseModel>>>() {});
    }

    public CompletableFuture<List<LicenseModel>> getLicensesBySoftware(Integer softwareId) {
        return apiClient.get("/api/licenses?softwareId=" + softwareId, new TypeReference<ApiResponseModel<List<LicenseModel>>>() {});
    }

    public CompletableFuture<List<LicenseModel>> getLicensesByStatus(String status) {
        return apiClient.get("/api/licenses?status=" + status, new TypeReference<ApiResponseModel<List<LicenseModel>>>() {});
    }

    public CompletableFuture<List<LicenseModel>> getExpiringLicenses(int days) {
        return apiClient.get("/api/licenses/expiring?days=" + days, new TypeReference<ApiResponseModel<List<LicenseModel>>>() {});
    }

    public CompletableFuture<LicenseModel> getLicenseById(Integer id) {
        return apiClient.get("/api/licenses/" + id, new TypeReference<ApiResponseModel<LicenseModel>>() {});
    }

    public CompletableFuture<LicenseModel> createLicense(LicenseModel license) {
        return apiClient.post("/api/licenses", license, new TypeReference<ApiResponseModel<LicenseModel>>() {});
    }

    public CompletableFuture<LicenseModel> updateLicense(Integer id, LicenseModel license) {
        return apiClient.put("/api/licenses/" + id, license, new TypeReference<ApiResponseModel<LicenseModel>>() {});
    }

    public CompletableFuture<Void> deleteLicense(Integer id) {
        return apiClient.delete("/api/licenses/" + id, new TypeReference<ApiResponseModel<Void>>() {});
    }
}
