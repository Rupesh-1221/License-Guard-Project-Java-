package com.licenseguard.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.licenseguard.frontend.model.ApiResponseModel;
import com.licenseguard.frontend.model.UserModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserApiClient {
    private final ApiClient apiClient = ApiClient.getInstance();

    public CompletableFuture<List<UserModel>> getAllUsers() {
        return apiClient.get("/api/users", new TypeReference<ApiResponseModel<List<UserModel>>>() {});
    }

    public CompletableFuture<List<UserModel>> getUsersByDepartment(Integer departmentId) {
        return apiClient.get("/api/users?departmentId=" + departmentId, new TypeReference<ApiResponseModel<List<UserModel>>>() {});
    }

    public CompletableFuture<UserModel> getUserById(Integer id) {
        return apiClient.get("/api/users/" + id, new TypeReference<ApiResponseModel<UserModel>>() {});
    }

    public CompletableFuture<UserModel> createUser(UserModel user) {
        return apiClient.post("/api/users", user, new TypeReference<ApiResponseModel<UserModel>>() {});
    }

    public CompletableFuture<UserModel> updateUser(Integer id, UserModel user) {
        return apiClient.put("/api/users/" + id, user, new TypeReference<ApiResponseModel<UserModel>>() {});
    }

    public CompletableFuture<Void> deleteUser(Integer id) {
        return apiClient.delete("/api/users/" + id, new TypeReference<ApiResponseModel<Void>>() {});
    }
}
