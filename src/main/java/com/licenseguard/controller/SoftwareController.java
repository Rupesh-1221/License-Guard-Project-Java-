package com.licenseguard.controller;

import com.licenseguard.dto.request.SoftwareRequest;
import com.licenseguard.dto.response.ApiResponse;
import com.licenseguard.dto.response.SoftwareResponse;
import com.licenseguard.service.SoftwareService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/software")
public class SoftwareController {

    private final SoftwareService softwareService;

    public SoftwareController(SoftwareService softwareService) {
        this.softwareService = softwareService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SoftwareResponse>>> getAllSoftware(
            @RequestParam(name = "vendorId", required = false) Integer vendorId) {
        List<SoftwareResponse> softwareList;
        if (vendorId != null) {
            softwareList = softwareService.getSoftwareByVendor(vendorId);
        } else {
            softwareList = softwareService.getAllSoftware();
        }
        return ResponseEntity.ok(ApiResponse.success("Software list retrieved successfully", softwareList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SoftwareResponse>> getSoftwareById(@PathVariable("id") Integer id) {
        SoftwareResponse software = softwareService.getSoftwareById(id);
        return ResponseEntity.ok(ApiResponse.success("Software retrieved successfully", software));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SoftwareResponse>> createSoftware(@Valid @RequestBody SoftwareRequest request) {
        SoftwareResponse created = softwareService.createSoftware(request);
        return new ResponseEntity<>(ApiResponse.success("Software created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SoftwareResponse>> updateSoftware(
            @PathVariable("id") Integer id,
            @Valid @RequestBody SoftwareRequest request) {
        SoftwareResponse updated = softwareService.updateSoftware(id, request);
        return ResponseEntity.ok(ApiResponse.success("Software updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSoftware(@PathVariable("id") Integer id) {
        softwareService.deleteSoftware(id);
        return ResponseEntity.ok(ApiResponse.success("Software deleted successfully", null));
    }
}
