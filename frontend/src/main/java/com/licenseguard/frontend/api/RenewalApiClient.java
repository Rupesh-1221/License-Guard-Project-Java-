package com.licenseguard.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.licenseguard.frontend.model.ApiResponseModel;
import com.licenseguard.frontend.model.RenewalModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RenewalApiClient {
    private final ApiClient apiClient = ApiClient.getInstance();

    public CompletableFuture<List<RenewalModel>> getAllRenewals() {
        return apiClient.get("/api/renewals", new TypeReference<ApiResponseModel<List<RenewalModel>>>() {});
    }

    public CompletableFuture<List<RenewalModel>> getRenewalsByLicense(Integer licenseId) {
        return apiClient.get("/api/renewals?licenseId=" + licenseId, new TypeReference<ApiResponseModel<List<RenewalModel>>>() {});
    }

    public CompletableFuture<RenewalModel> getRenewalById(Integer id) {
        return apiClient.get("/api/renewals/" + id, new TypeReference<ApiResponseModel<RenewalModel>>() {});
    }

    public CompletableFuture<RenewalModel> renewLicense(RenewalModel renewal) {
        return apiClient.post("/api/renewals", renewal, new TypeReference<ApiResponseModel<RenewalModel>>() {});
    }

    public CompletableFuture<Void> deleteRenewal(Integer id) {
        return apiClient.delete("/api/renewals/" + id, new TypeReference<ApiResponseModel<Void>>() {});
    }
}
