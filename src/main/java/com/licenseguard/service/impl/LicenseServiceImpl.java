package com.licenseguard.service.impl;

import com.licenseguard.dto.request.LicenseRequest;
import com.licenseguard.dto.response.LicenseResponse;
import com.licenseguard.entity.License;
import com.licenseguard.entity.Software;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.exception.ResourceNotFoundException;
import com.licenseguard.repository.LicenseRepository;
import com.licenseguard.repository.SoftwareRepository;
import com.licenseguard.service.LicenseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LicenseServiceImpl implements LicenseService {

    private final LicenseRepository licenseRepository;
    private final SoftwareRepository softwareRepository;

    public LicenseServiceImpl(LicenseRepository licenseRepository, SoftwareRepository softwareRepository) {
        this.licenseRepository = licenseRepository;
        this.softwareRepository = softwareRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseResponse> getAllLicenses() {
        return licenseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LicenseResponse getLicenseById(Integer licenseId) {
        License license = findLicenseEntityById(licenseId);
        return mapToResponse(license);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseResponse> getLicensesBySoftware(Integer softwareId) {
        return licenseRepository.findBySoftwareSoftwareId(softwareId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseResponse> getLicensesByStatus(String status) {
        return licenseRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseResponse> getExpiringLicenses(int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        return licenseRepository.findByExpiryDateBetween(startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LicenseResponse createLicense(LicenseRequest request) {
        if (licenseRepository.existsByLicenseKey(request.getLicenseKey())) {
            throw new DuplicateResourceException("License with key '" + request.getLicenseKey() + "' already exists");
        }

        Software software = softwareRepository.findById(request.getSoftwareId())
                .orElseThrow(() -> new ResourceNotFoundException("Software not found with id: " + request.getSoftwareId()));

        if (request.getStartDate() != null && request.getPurchaseDate() != null && request.getStartDate().isBefore(request.getPurchaseDate())) {
            throw new com.licenseguard.exception.BadRequestException("Start date cannot be before purchase date");
        }
        if (request.getExpiryDate() != null && request.getStartDate() != null && request.getExpiryDate().isBefore(request.getStartDate())) {
            throw new com.licenseguard.exception.BadRequestException("Expiry date cannot be before start date");
        }

        License license = new License();
        license.setSoftware(software);
        license.setLicenseKey(request.getLicenseKey());
        license.setLicenseType(request.getLicenseType());
        license.setPurchaseDate(request.getPurchaseDate());
        license.setStartDate(request.getStartDate());
        license.setExpiryDate(request.getExpiryDate());
        license.setTotalSeats(request.getTotalSeats());
        license.setAvailableSeats(request.getAvailableSeats() != null ? request.getAvailableSeats() : request.getTotalSeats());
        license.setCost(request.getCost());
        license.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

        License saved = licenseRepository.save(license);
        return mapToResponse(saved);
    }

    @Override
    public LicenseResponse updateLicense(Integer licenseId, LicenseRequest request) {
        License license = findLicenseEntityById(licenseId);

        if (!license.getLicenseKey().equalsIgnoreCase(request.getLicenseKey()) &&
                licenseRepository.existsByLicenseKey(request.getLicenseKey())) {
            throw new DuplicateResourceException("License with key '" + request.getLicenseKey() + "' already exists");
        }

        Software software = softwareRepository.findById(request.getSoftwareId())
                .orElseThrow(() -> new ResourceNotFoundException("Software not found with id: " + request.getSoftwareId()));

        if (request.getStartDate() != null && request.getPurchaseDate() != null && request.getStartDate().isBefore(request.getPurchaseDate())) {
            throw new com.licenseguard.exception.BadRequestException("Start date cannot be before purchase date");
        }
        if (request.getExpiryDate() != null && request.getStartDate() != null && request.getExpiryDate().isBefore(request.getStartDate())) {
            throw new com.licenseguard.exception.BadRequestException("Expiry date cannot be before start date");
        }

        license.setSoftware(software);
        license.setLicenseKey(request.getLicenseKey());
        license.setLicenseType(request.getLicenseType());
        license.setPurchaseDate(request.getPurchaseDate());
        license.setStartDate(request.getStartDate());
        license.setExpiryDate(request.getExpiryDate());
        license.setTotalSeats(request.getTotalSeats());
        if (request.getAvailableSeats() != null) {
            license.setAvailableSeats(request.getAvailableSeats());
        }
        license.setCost(request.getCost());
        if (request.getStatus() != null) {
            license.setStatus(request.getStatus());
        }

        License updated = licenseRepository.save(license);
        return mapToResponse(updated);
    }

    @Override
    public void deleteLicense(Integer licenseId) {
        License license = findLicenseEntityById(licenseId);
        
        if (license.getLicenseAssignments() != null && !license.getLicenseAssignments().isEmpty()) {
            throw new com.licenseguard.exception.BadRequestException("Cannot delete license because it has assignments.");
        }
        
        if (license.getRenewals() != null && !license.getRenewals().isEmpty()) {
            throw new com.licenseguard.exception.BadRequestException("Cannot delete license because it has renewals.");
        }
        
        licenseRepository.delete(license);
    }

    private License findLicenseEntityById(Integer licenseId) {
        return licenseRepository.findById(licenseId)
                .orElseThrow(() -> new ResourceNotFoundException("License not found with id: " + licenseId));
    }

    private LicenseResponse mapToResponse(License license) {
        Integer softwareId = license.getSoftware() != null ? license.getSoftware().getSoftwareId() : null;
        String softwareName = license.getSoftware() != null ? license.getSoftware().getSoftwareName() : null;
        Integer vendorId = null;
        String vendorName = null;

        if (license.getSoftware() != null && license.getSoftware().getVendor() != null) {
            vendorId = license.getSoftware().getVendor().getVendorId();
            vendorName = license.getSoftware().getVendor().getVendorName();
        }

        return new LicenseResponse(
                license.getLicenseId(),
                license.getLicenseKey(),
                license.getLicenseType(),
                license.getPurchaseDate(),
                license.getStartDate(),
                license.getExpiryDate(),
                license.getTotalSeats(),
                license.getAvailableSeats(),
                license.getCost(),
                license.getStatus(),
                softwareId,
                softwareName,
                vendorId,
                vendorName
        );
    }
}
