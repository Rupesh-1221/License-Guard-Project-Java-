package com.licenseguard.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorModel {
    private Integer vendorId;
    private String vendorName;
    private String contactEmail;
    private String website;
    private Integer softwareCount;

    public VendorModel() {
    }

    public VendorModel(Integer vendorId, String vendorName, String contactEmail, String website, Integer softwareCount) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.contactEmail = contactEmail;
        this.website = website;
        this.softwareCount = softwareCount;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Integer getSoftwareCount() {
        return softwareCount;
    }

    public void setSoftwareCount(Integer softwareCount) {
        this.softwareCount = softwareCount;
    }

    @Override
    public String toString() {
        return vendorName != null ? vendorName : "";
    }
}
