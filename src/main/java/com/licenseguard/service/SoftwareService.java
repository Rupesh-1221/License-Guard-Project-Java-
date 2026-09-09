package com.licenseguard.service;

import com.licenseguard.dto.request.SoftwareRequest;
import com.licenseguard.dto.response.SoftwareResponse;

import java.util.List;

public interface SoftwareService {
    List<SoftwareResponse> getAllSoftware();
    SoftwareResponse getSoftwareById(Integer softwareId);
    List<SoftwareResponse> getSoftwareByVendor(Integer vendorId);
    SoftwareResponse createSoftware(SoftwareRequest request);
    SoftwareResponse updateSoftware(Integer softwareId, SoftwareRequest request);
    void deleteSoftware(Integer softwareId);
}
