package com.licenseguard.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VendorRequest {

    @NotBlank(message = "Vendor name is required")
    @Size(max = 100, message = "Vendor name cannot exceed 100 characters")
    private String vendorName;

    @Email(message = "Contact email format is invalid")
    @Size(max = 150, message = "Contact email cannot exceed 150 characters")
    private String contactEmail;

    @Size(max = 255, message = "Website cannot exceed 255 characters")
    private String website;

    public VendorRequest() {
    }

    public VendorRequest(String vendorName, String contactEmail, String website) {
        this.vendorName = vendorName;
        this.contactEmail = contactEmail;
        this.website = website;
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
}
