package com.licenseguard.dto.response;

public class VendorResponse {
    private Integer vendorId;
    private String vendorName;
    private String contactEmail;
    private String website;
    private Integer softwareCount;

    public VendorResponse() {
    }

    public VendorResponse(Integer vendorId, String vendorName, String contactEmail, String website, Integer softwareCount) {
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
}
