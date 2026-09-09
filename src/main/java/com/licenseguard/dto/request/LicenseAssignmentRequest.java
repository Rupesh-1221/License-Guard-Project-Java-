package com.licenseguard.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class LicenseAssignmentRequest {

    @NotNull(message = "License ID is required")
    private Integer licenseId;

    @NotNull(message = "User ID is required")
    private Integer userId;

    private LocalDate assignedDate;

    public LicenseAssignmentRequest() {
    }

    public LicenseAssignmentRequest(Integer licenseId, Integer userId, LocalDate assignedDate) {
        this.licenseId = licenseId;
        this.userId = userId;
        this.assignedDate = assignedDate;
    }

    public Integer getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Integer licenseId) {
        this.licenseId = licenseId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }
}
