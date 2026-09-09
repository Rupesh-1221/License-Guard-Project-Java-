package com.licenseguard.controller;

import com.licenseguard.dto.request.LicenseRequest;
import com.licenseguard.dto.response.ApiResponse;
import com.licenseguard.dto.response.LicenseResponse;
import com.licenseguard.service.LicenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/licenses")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LicenseResponse>>> getAllLicenses(
            @RequestParam(name = "softwareId", required = false) Integer softwareId,
            @RequestParam(name = "status", required = false) String status) {
        List<LicenseResponse> licenses;
        if (softwareId != null) {
            licenses = licenseService.getLicensesBySoftware(softwareId);
        } else if (status != null) {
            licenses = licenseService.getLicensesByStatus(status);
        } else {
            licenses = licenseService.getAllLicenses();
        }
        return ResponseEntity.ok(ApiResponse.success("Licenses retrieved successfully", licenses));
    }

    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<LicenseResponse>>> getExpiringLicenses(
            @RequestParam(name = "days", defaultValue = "30") int days) {
        List<LicenseResponse> licenses = licenseService.getExpiringLicenses(days);
        return ResponseEntity.ok(ApiResponse.success("Expiring licenses retrieved successfully", licenses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LicenseResponse>> getLicenseById(@PathVariable("id") Integer id) {
        LicenseResponse license = licenseService.getLicenseById(id);
        return ResponseEntity.ok(ApiResponse.success("License retrieved successfully", license));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LicenseResponse>> createLicense(@Valid @RequestBody LicenseRequest request) {
        LicenseResponse created = licenseService.createLicense(request);
        return new ResponseEntity<>(ApiResponse.success("License created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LicenseResponse>> updateLicense(
            @PathVariable("id") Integer id,
            @Valid @RequestBody LicenseRequest request) {
        LicenseResponse updated = licenseService.updateLicense(id, request);
        return ResponseEntity.ok(ApiResponse.success("License updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLicense(@PathVariable("id") Integer id) {
        licenseService.deleteLicense(id);
        return ResponseEntity.ok(ApiResponse.success("License deleted successfully", null));
    }
}
