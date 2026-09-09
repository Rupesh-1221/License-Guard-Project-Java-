package com.licenseguard.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LicenseRequest {

    @NotNull(message = "Software ID is required")
    private Integer softwareId;

    @NotBlank(message = "License key is required")
    @Size(max = 255, message = "License key cannot exceed 255 characters")
    private String licenseKey;

    @NotBlank(message = "License type is required")
    @Size(max = 50, message = "License type cannot exceed 50 characters")
    private String licenseType;

    private LocalDate purchaseDate;

    private LocalDate startDate;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;

    private Integer availableSeats;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost must be positive or zero")
    private BigDecimal cost;

    private String status;

    public LicenseRequest() {
    }

    public LicenseRequest(Integer softwareId, String licenseKey, String licenseType, LocalDate purchaseDate,
                          LocalDate startDate, LocalDate expiryDate, Integer totalSeats, Integer availableSeats,
                          BigDecimal cost, String status) {
        this.softwareId = softwareId;
        this.licenseKey = licenseKey;
        this.licenseType = licenseType;
        this.purchaseDate = purchaseDate;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.cost = cost;
        this.status = status;
    }

    public Integer getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(Integer softwareId) {
        this.softwareId = softwareId;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
