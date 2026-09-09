package com.licenseguard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SoftwareRequest {

    @NotBlank(message = "Software name is required")
    @Size(max = 100, message = "Software name cannot exceed 100 characters")
    private String softwareName;

    @Size(max = 50, message = "Version cannot exceed 50 characters")
    private String version;

    private Integer vendorId;

    private String description;

    public SoftwareRequest() {
    }

    public SoftwareRequest(String softwareName, String version, Integer vendorId, String description) {
        this.softwareName = softwareName;
        this.version = version;
        this.vendorId = vendorId;
        this.description = description;
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

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
