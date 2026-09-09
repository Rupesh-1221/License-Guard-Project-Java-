package com.licenseguard.controller;

import com.licenseguard.dto.request.LicenseAssignmentRequest;
import com.licenseguard.dto.response.ApiResponse;
import com.licenseguard.dto.response.LicenseAssignmentResponse;
import com.licenseguard.service.LicenseAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/license-assignments")
public class LicenseAssignmentController {

    private final LicenseAssignmentService assignmentService;

    public LicenseAssignmentController(LicenseAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LicenseAssignmentResponse>>> getAllAssignments(
            @RequestParam(name = "licenseId", required = false) Integer licenseId,
            @RequestParam(name = "userId", required = false) Integer userId) {
        List<LicenseAssignmentResponse> assignments;
        if (licenseId != null) {
            assignments = assignmentService.getAssignmentsByLicense(licenseId);
        } else if (userId != null) {
            assignments = assignmentService.getAssignmentsByUser(userId);
        } else {
            assignments = assignmentService.getAllAssignments();
        }
        return ResponseEntity.ok(ApiResponse.success("License assignments retrieved successfully", assignments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LicenseAssignmentResponse>> getAssignmentById(@PathVariable("id") Integer id) {
        LicenseAssignmentResponse assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(ApiResponse.success("License assignment retrieved successfully", assignment));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LicenseAssignmentResponse>> assignLicense(
            @Valid @RequestBody LicenseAssignmentRequest request) {
        LicenseAssignmentResponse assigned = assignmentService.assignLicense(request);
        return new ResponseEntity<>(ApiResponse.success("License assigned successfully", assigned), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/unassign")
    public ResponseEntity<ApiResponse<LicenseAssignmentResponse>> unassignLicense(@PathVariable("id") Integer id) {
        LicenseAssignmentResponse unassigned = assignmentService.unassignLicense(id);
        return ResponseEntity.ok(ApiResponse.success("License unassigned successfully", unassigned));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable("id") Integer id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponse.success("License assignment deleted successfully", null));
    }
}
