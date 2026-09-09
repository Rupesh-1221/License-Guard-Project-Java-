package com.licenseguard.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.licenseguard.frontend.model.ApiResponseModel;
import com.licenseguard.frontend.model.ErrorResponseModel;
import com.licenseguard.frontend.util.AlertUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";
    private static ApiClient instance;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public <T> CompletableFuture<T> get(String path, TypeReference<ApiResponseModel<T>> typeRef) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();

        return sendAsync(request, typeRef);
    }

    public <T> CompletableFuture<T> post(String path, Object body, TypeReference<ApiResponseModel<T>> typeRef) {
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            return sendAsync(request, typeRef);
        } catch (Exception e) {
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    public <T> CompletableFuture<T> put(String path, Object body, TypeReference<ApiResponseModel<T>> typeRef) {
        try {
            String jsonBody = body != null ? objectMapper.writeValueAsString(body) : "";
            HttpRequest.BodyPublisher publisher = body != null ?
                    HttpRequest.BodyPublishers.ofString(jsonBody) :
                    HttpRequest.BodyPublishers.noBody();

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .PUT(publisher);

            if (body != null) {
                builder.header("Content-Type", "application/json");
            }

            return sendAsync(builder.build(), typeRef);
        } catch (Exception e) {
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    public <T> CompletableFuture<T> delete(String path, TypeReference<ApiResponseModel<T>> typeRef) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .DELETE()
                .build();

        return sendAsync(request, typeRef);
    }

    public CompletableFuture<Boolean> checkBackendHealth() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/departments"))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() >= 200 && response.statusCode() < 500)
                .exceptionally(ex -> false);
    }

    private <T> CompletableFuture<T> sendAsync(HttpRequest request, TypeReference<ApiResponseModel<T>> typeRef) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    int statusCode = response.statusCode();
                    String body = response.body();

                    if (statusCode >= 200 && statusCode < 300) {
                        try {
                            ApiResponseModel<T> apiResponse = objectMapper.readValue(body, typeRef);
                            return CompletableFuture.completedFuture(apiResponse.getData());
                        } catch (IOException e) {
                            return CompletableFuture.failedFuture(new RuntimeException("Failed to parse response: " + e.getMessage(), e));
                        }
                    } else {
                        try {
                            ErrorResponseModel errorResponse = objectMapper.readValue(body, ErrorResponseModel.class);
                            AlertUtils.showBackendApiError(errorResponse);
                            return CompletableFuture.failedFuture(new ApiException(errorResponse.getMessage(), statusCode));
                        } catch (Exception e) {
                            String errorMsg = "Server error (HTTP " + statusCode + "): " + body;
                            AlertUtils.showError("Server Error", errorMsg);
                            return CompletableFuture.failedFuture(new ApiException(errorMsg, statusCode));
                        }
                    }
                })
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof ConnectException) {
                        String msg = "Unable to connect to LicenseGuard server at " + BASE_URL + ".\nPlease ensure the Spring Boot backend is running.";
                        AlertUtils.showError("Connection Failed", msg);
                    } else if (!(cause instanceof ApiException)) {
                        AlertUtils.showError("Request Error", cause.getMessage());
                    }
                    throw new RuntimeException(cause);
                });
    }

    public static class ApiException extends RuntimeException {
        private final int statusCode;

        public ApiException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
