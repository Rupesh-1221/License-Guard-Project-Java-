package com.licenseguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "licenses")
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "license_id")
    private Integer licenseId;

    @Column(name = "license_key", length = 255, nullable = false, unique = true)
    private String licenseKey;

    @Column(name = "license_type", length = 50, nullable = false)
    private String licenseType;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "status", length = 30)
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "software_id", nullable = false, foreignKey = @ForeignKey(name = "fk_license_software"))
    private Software software;

    @OneToMany(mappedBy = "license", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LicenseAssignment> licenseAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "license", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Renewal> renewals = new ArrayList<>();

    public License() {
    }

    public License(String licenseKey, String licenseType, LocalDate purchaseDate, LocalDate startDate,
                   LocalDate expiryDate, Integer totalSeats, Integer availableSeats, BigDecimal cost,
                   String status, Software software) {
        this.licenseKey = licenseKey;
        this.licenseType = licenseType;
        this.purchaseDate = purchaseDate;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.cost = cost;
        this.status = status != null ? status : "ACTIVE";
        this.software = software;
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

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public List<LicenseAssignment> getLicenseAssignments() {
        return licenseAssignments;
    }

    public void setLicenseAssignments(List<LicenseAssignment> licenseAssignments) {
        this.licenseAssignments = licenseAssignments;
    }

    public List<Renewal> getRenewals() {
        return renewals;
    }

    public void setRenewals(List<Renewal> renewals) {
        this.renewals = renewals;
    }
}
