package com.licenseguard.service;

import com.licenseguard.dto.request.VendorRequest;
import com.licenseguard.dto.response.VendorResponse;

import java.util.List;

public interface VendorService {
    List<VendorResponse> getAllVendors();
    VendorResponse getVendorById(Integer vendorId);
    VendorResponse createVendor(VendorRequest request);
    VendorResponse updateVendor(Integer vendorId, VendorRequest request);
    void deleteVendor(Integer vendorId);
}
