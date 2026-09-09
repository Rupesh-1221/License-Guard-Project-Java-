package com.licenseguard.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RenewalRequest {

    @NotNull(message = "License ID is required")
    private Integer licenseId;

    @NotNull(message = "New expiry date is required")
    private LocalDate newExpiryDate;

    private LocalDate renewalDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Renewal cost must be positive or zero")
    private BigDecimal renewalCost;

    private Integer renewedBy;

    @Size(max = 255, message = "Remarks cannot exceed 255 characters")
    private String remarks;

    public RenewalRequest() {
    }

    public RenewalRequest(Integer licenseId, LocalDate newExpiryDate, LocalDate renewalDate,
                          BigDecimal renewalCost, Integer renewedBy, String remarks) {
        this.licenseId = licenseId;
        this.newExpiryDate = newExpiryDate;
        this.renewalDate = renewalDate;
        this.renewalCost = renewalCost;
        this.renewedBy = renewedBy;
        this.remarks = remarks;
    }

    public Integer getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Integer licenseId) {
        this.licenseId = licenseId;
    }

    public LocalDate getNewExpiryDate() {
        return newExpiryDate;
    }

    public void setNewExpiryDate(LocalDate newExpiryDate) {
        this.newExpiryDate = newExpiryDate;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }

    public BigDecimal getRenewalCost() {
        return renewalCost;
    }

    public void setRenewalCost(BigDecimal renewalCost) {
        this.renewalCost = renewalCost;
    }

    public Integer getRenewedBy() {
        return renewedBy;
    }

    public void setRenewedBy(Integer renewedBy) {
        this.renewedBy = renewedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
