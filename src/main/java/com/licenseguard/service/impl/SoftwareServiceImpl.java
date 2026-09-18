package com.licenseguard.service.impl;

import com.licenseguard.dto.request.SoftwareRequest;
import com.licenseguard.dto.response.SoftwareResponse;
import com.licenseguard.entity.Software;
import com.licenseguard.entity.Vendor;
import com.licenseguard.exception.ResourceNotFoundException;
import com.licenseguard.repository.SoftwareRepository;
import com.licenseguard.repository.VendorRepository;
import com.licenseguard.service.SoftwareService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SoftwareServiceImpl implements SoftwareService {

    private final SoftwareRepository softwareRepository;
    private final VendorRepository vendorRepository;

    public SoftwareServiceImpl(SoftwareRepository softwareRepository, VendorRepository vendorRepository) {
        this.softwareRepository = softwareRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoftwareResponse> getAllSoftware() {
        return softwareRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SoftwareResponse getSoftwareById(Integer softwareId) {
        Software software = findSoftwareEntityById(softwareId);
        return mapToResponse(software);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoftwareResponse> getSoftwareByVendor(Integer vendorId) {
        return softwareRepository.findByVendorVendorId(vendorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SoftwareResponse createSoftware(SoftwareRequest request) {
        Vendor vendor = null;
        if (request.getVendorId() != null) {
            vendor = vendorRepository.findById(request.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + request.getVendorId()));
        }

        if (vendor != null && softwareRepository.existsBySoftwareNameIgnoreCaseAndVersionAndVendorVendorId(
                request.getSoftwareName(), request.getVersion(), vendor.getVendorId())) {
            throw new com.licenseguard.exception.DuplicateResourceException("Software '" + request.getSoftwareName() + "' version '" + request.getVersion() + "' already exists for this vendor");
        }

        Software software = new Software();
        software.setSoftwareName(request.getSoftwareName());
        software.setVersion(request.getVersion());
        software.setDescription(request.getDescription());
        software.setVendor(vendor);

        Software saved = softwareRepository.save(software);
        return mapToResponse(saved);
    }

    @Override
    public SoftwareResponse updateSoftware(Integer softwareId, SoftwareRequest request) {
        Software software = findSoftwareEntityById(softwareId);

        Vendor vendor = null;
        if (request.getVendorId() != null) {
            vendor = vendorRepository.findById(request.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + request.getVendorId()));
        }

        boolean nameChanged = !software.getSoftwareName().equalsIgnoreCase(request.getSoftwareName());
        boolean versionChanged = !software.getVersion().equalsIgnoreCase(request.getVersion());
        boolean vendorChanged = vendor != null && (software.getVendor() == null || !software.getVendor().getVendorId().equals(vendor.getVendorId()));

        if ((nameChanged || versionChanged || vendorChanged) && vendor != null &&
                softwareRepository.existsBySoftwareNameIgnoreCaseAndVersionAndVendorVendorId(
                        request.getSoftwareName(), request.getVersion(), vendor.getVendorId())) {
            throw new com.licenseguard.exception.DuplicateResourceException("Software '" + request.getSoftwareName() + "' version '" + request.getVersion() + "' already exists for this vendor");
        }

        software.setSoftwareName(request.getSoftwareName());
        software.setVersion(request.getVersion());
        software.setDescription(request.getDescription());
        software.setVendor(vendor);

        Software updated = softwareRepository.save(software);
        return mapToResponse(updated);
    }

    @Override
    public void deleteSoftware(Integer softwareId) {
        Software software = findSoftwareEntityById(softwareId);
        if (software.getLicenses() != null && !software.getLicenses().isEmpty()) {
            throw new com.licenseguard.exception.BadRequestException("Cannot delete software because it has associated licenses.");
        }
        softwareRepository.delete(software);
    }

    private Software findSoftwareEntityById(Integer softwareId) {
        return softwareRepository.findById(softwareId)
                .orElseThrow(() -> new ResourceNotFoundException("Software not found with id: " + softwareId));
    }

    private SoftwareResponse mapToResponse(Software software) {
        Integer vendorId = software.getVendor() != null ? software.getVendor().getVendorId() : null;
        String vendorName = software.getVendor() != null ? software.getVendor().getVendorName() : null;
        int licenseCount = software.getLicenses() != null ? software.getLicenses().size() : 0;

        return new SoftwareResponse(
                software.getSoftwareId(),
                software.getSoftwareName(),
                software.getVersion(),
                software.getDescription(),
                vendorId,
                vendorName,
                licenseCount
        );
    }
}
