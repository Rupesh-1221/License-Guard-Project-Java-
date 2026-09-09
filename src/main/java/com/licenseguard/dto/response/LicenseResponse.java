package com.licenseguard.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LicenseResponse {
    private Integer licenseId;
    private String licenseKey;
    private String licenseType;
    private LocalDate purchaseDate;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal cost;
    private String status;
    private Integer softwareId;
    private String softwareName;
    private Integer vendorId;
    private String vendorName;

    public LicenseResponse() {
    }

    public LicenseResponse(Integer licenseId, String licenseKey, String licenseType, LocalDate purchaseDate,
                           LocalDate startDate, LocalDate expiryDate, Integer totalSeats, Integer availableSeats,
                           BigDecimal cost, String status, Integer softwareId, String softwareName,
                           Integer vendorId, String vendorName) {
        this.licenseId = licenseId;
        this.licenseKey = licenseKey;
        this.licenseType = licenseType;
        this.purchaseDate = purchaseDate;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.cost = cost;
        this.status = status;
        this.softwareId = softwareId;
        this.softwareName = softwareName;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
    }

    public Integer getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Integer licenseId) {
        this.licenseId = licenseId;
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

    public Integer getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(Integer softwareId) {
        this.softwareId = softwareId;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
