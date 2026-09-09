package com.licenseguard.frontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RenewalModel {
    private Integer renewalId;
    private Integer licenseId;
    private String licenseKey;
    private String softwareName;
    private LocalDate oldExpiryDate;
    private LocalDate newExpiryDate;
    private LocalDate renewalDate;
    private BigDecimal renewalCost;

    @JsonProperty("renewedBy")
    @JsonAlias({"renewedByUserId", "renewedBy"})
    private Integer renewedByUserId;
    private String renewedByUserName;
    private String remarks;

    public RenewalModel() {
    }

    public RenewalModel(Integer renewalId, Integer licenseId, String licenseKey, String softwareName, LocalDate oldExpiryDate, LocalDate newExpiryDate, LocalDate renewalDate, BigDecimal renewalCost, Integer renewedByUserId, String renewedByUserName, String remarks) {
        this.renewalId = renewalId;
        this.licenseId = licenseId;
        this.licenseKey = licenseKey;
        this.softwareName = softwareName;
        this.oldExpiryDate = oldExpiryDate;
        this.newExpiryDate = newExpiryDate;
        this.renewalDate = renewalDate;
        this.renewalCost = renewalCost;
        this.renewedByUserId = renewedByUserId;
        this.renewedByUserName = renewedByUserName;
        this.remarks = remarks;
    }

    public Integer getRenewalId() {
        return renewalId;
    }

    public void setRenewalId(Integer renewalId) {
        this.renewalId = renewalId;
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

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public LocalDate getOldExpiryDate() {
        return oldExpiryDate;
    }

    public void setOldExpiryDate(LocalDate oldExpiryDate) {
        this.oldExpiryDate = oldExpiryDate;
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

    public Integer getRenewedByUserId() {
        return renewedByUserId;
    }

    public void setRenewedByUserId(Integer renewedByUserId) {
        this.renewedByUserId = renewedByUserId;
    }

    public String getRenewedByUserName() {
        return renewedByUserName;
    }

    public void setRenewedByUserName(String renewedByUserName) {
        this.renewedByUserName = renewedByUserName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
