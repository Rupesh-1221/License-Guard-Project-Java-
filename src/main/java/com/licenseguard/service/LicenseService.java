package com.licenseguard.service;

import com.licenseguard.dto.request.LicenseRequest;
import com.licenseguard.dto.response.LicenseResponse;

import java.time.LocalDate;
import java.util.List;

public interface LicenseService {
    List<LicenseResponse> getAllLicenses();
    LicenseResponse getLicenseById(Integer licenseId);
    List<LicenseResponse> getLicensesBySoftware(Integer softwareId);
    List<LicenseResponse> getLicensesByStatus(String status);
    List<LicenseResponse> getExpiringLicenses(int days);
    LicenseResponse createLicense(LicenseRequest request);
    LicenseResponse updateLicense(Integer licenseId, LicenseRequest request);
    void deleteLicense(Integer licenseId);
}
