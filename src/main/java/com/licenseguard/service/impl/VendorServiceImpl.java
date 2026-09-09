package com.licenseguard.service.impl;

import com.licenseguard.dto.request.VendorRequest;
import com.licenseguard.dto.response.VendorResponse;
import com.licenseguard.entity.Vendor;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.exception.ResourceNotFoundException;
import com.licenseguard.repository.VendorRepository;
import com.licenseguard.service.VendorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    public VendorServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        return vendorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(Integer vendorId) {
        Vendor vendor = findVendorEntityById(vendorId);
        return mapToResponse(vendor);
    }

    @Override
    public VendorResponse createVendor(VendorRequest request) {
        if (vendorRepository.existsByVendorNameIgnoreCase(request.getVendorName())) {
            throw new DuplicateResourceException("Vendor with name '" + request.getVendorName() + "' already exists");
        }

        Vendor vendor = new Vendor();
        vendor.setVendorName(request.getVendorName());
        vendor.setContactEmail(request.getContactEmail());
        vendor.setWebsite(request.getWebsite());

        Vendor saved = vendorRepository.save(vendor);
        return mapToResponse(saved);
    }

    @Override
    public VendorResponse updateVendor(Integer vendorId, VendorRequest request) {
        Vendor vendor = findVendorEntityById(vendorId);

        if (!vendor.getVendorName().equalsIgnoreCase(request.getVendorName()) &&
                vendorRepository.existsByVendorNameIgnoreCase(request.getVendorName())) {
            throw new DuplicateResourceException("Vendor with name '" + request.getVendorName() + "' already exists");
        }

        vendor.setVendorName(request.getVendorName());
        vendor.setContactEmail(request.getContactEmail());
        vendor.setWebsite(request.getWebsite());

        Vendor updated = vendorRepository.save(vendor);
        return mapToResponse(updated);
    }

    @Override
    public void deleteVendor(Integer vendorId) {
        Vendor vendor = findVendorEntityById(vendorId);
        vendorRepository.delete(vendor);
    }

    private Vendor findVendorEntityById(Integer vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));
    }

    private VendorResponse mapToResponse(Vendor vendor) {
        int softwareCount = vendor.getSoftwareList() != null ? vendor.getSoftwareList().size() : 0;
        return new VendorResponse(
                vendor.getVendorId(),
                vendor.getVendorName(),
                vendor.getContactEmail(),
                vendor.getWebsite(),
                softwareCount
        );
    }
}
