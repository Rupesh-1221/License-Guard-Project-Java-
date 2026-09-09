package com.licenseguard.dto.response;

public class SoftwareResponse {
    private Integer softwareId;
    private String softwareName;
    private String version;
    private String description;
    private Integer vendorId;
    private String vendorName;
    private Integer licenseCount;

    public SoftwareResponse() {
    }

    public SoftwareResponse(Integer softwareId, String softwareName, String version, String description,
                            Integer vendorId, String vendorName, Integer licenseCount) {
        this.softwareId = softwareId;
        this.softwareName = softwareName;
        this.version = version;
        this.description = description;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.licenseCount = licenseCount;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getLicenseCount() {
        return licenseCount;
    }

    public void setLicenseCount(Integer licenseCount) {
        this.licenseCount = licenseCount;
    }
}
