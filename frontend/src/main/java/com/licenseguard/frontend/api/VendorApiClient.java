package com.licenseguard.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.licenseguard.frontend.model.ApiResponseModel;
import com.licenseguard.frontend.model.VendorModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VendorApiClient {
    private final ApiClient apiClient = ApiClient.getInstance();

    public CompletableFuture<List<VendorModel>> getAllVendors() {
        return apiClient.get("/api/vendors", new TypeReference<ApiResponseModel<List<VendorModel>>>() {});
    }

    public CompletableFuture<VendorModel> getVendorById(Integer id) {
        return apiClient.get("/api/vendors/" + id, new TypeReference<ApiResponseModel<VendorModel>>() {});
    }

    public CompletableFuture<VendorModel> createVendor(VendorModel vendor) {
        return apiClient.post("/api/vendors", vendor, new TypeReference<ApiResponseModel<VendorModel>>() {});
    }

    public CompletableFuture<VendorModel> updateVendor(Integer id, VendorModel vendor) {
        return apiClient.put("/api/vendors/" + id, vendor, new TypeReference<ApiResponseModel<VendorModel>>() {});
    }

    public CompletableFuture<Void> deleteVendor(Integer id) {
        return apiClient.delete("/api/vendors/" + id, new TypeReference<ApiResponseModel<Void>>() {});
    }
}
