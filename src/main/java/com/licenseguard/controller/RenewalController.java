package com.licenseguard.controller;

import com.licenseguard.dto.request.RenewalRequest;
import com.licenseguard.dto.response.ApiResponse;
import com.licenseguard.dto.response.RenewalResponse;
import com.licenseguard.service.RenewalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/renewals")
public class RenewalController {

    private final RenewalService renewalService;

    public RenewalController(RenewalService renewalService) {
        this.renewalService = renewalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RenewalResponse>>> getAllRenewals(
            @RequestParam(name = "licenseId", required = false) Integer licenseId) {
        List<RenewalResponse> renewals;
        if (licenseId != null) {
            renewals = renewalService.getRenewalsByLicense(licenseId);
        } else {
            renewals = renewalService.getAllRenewals();
        }
        return ResponseEntity.ok(ApiResponse.success("Renewals retrieved successfully", renewals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RenewalResponse>> getRenewalById(@PathVariable("id") Integer id) {
        RenewalResponse renewal = renewalService.getRenewalById(id);
        return ResponseEntity.ok(ApiResponse.success("Renewal retrieved successfully", renewal));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RenewalResponse>> renewLicense(@Valid @RequestBody RenewalRequest request) {
        RenewalResponse renewed = renewalService.renewLicense(request);
        return new ResponseEntity<>(ApiResponse.success("License renewed successfully", renewed), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRenewal(@PathVariable("id") Integer id) {
        renewalService.deleteRenewal(id);
        return ResponseEntity.ok(ApiResponse.success("Renewal deleted successfully", null));
    }
}
