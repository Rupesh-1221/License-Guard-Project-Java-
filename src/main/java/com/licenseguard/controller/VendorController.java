package com.licenseguard.controller;

import com.licenseguard.dto.request.VendorRequest;
import com.licenseguard.dto.response.ApiResponse;
import com.licenseguard.dto.response.VendorResponse;
import com.licenseguard.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getAllVendors() {
        List<VendorResponse> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(ApiResponse.success("Vendors retrieved successfully", vendors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorById(@PathVariable("id") Integer id) {
        VendorResponse vendor = vendorService.getVendorById(id);
        return ResponseEntity.ok(ApiResponse.success("Vendor retrieved successfully", vendor));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VendorResponse>> createVendor(@Valid @RequestBody VendorRequest request) {
        VendorResponse created = vendorService.createVendor(request);
        return new ResponseEntity<>(ApiResponse.success("Vendor created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> updateVendor(
            @PathVariable("id") Integer id,
            @Valid @RequestBody VendorRequest request) {
        VendorResponse updated = vendorService.updateVendor(id, request);
        return ResponseEntity.ok(ApiResponse.success("Vendor updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable("id") Integer id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.ok(ApiResponse.success("Vendor deleted successfully", null));
    }
}
