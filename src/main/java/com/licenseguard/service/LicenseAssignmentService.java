package com.licenseguard.service;

import com.licenseguard.dto.request.LicenseAssignmentRequest;
import com.licenseguard.dto.response.LicenseAssignmentResponse;

import java.util.List;

public interface LicenseAssignmentService {
    List<LicenseAssignmentResponse> getAllAssignments();
    LicenseAssignmentResponse getAssignmentById(Integer assignmentId);
    List<LicenseAssignmentResponse> getAssignmentsByLicense(Integer licenseId);
    List<LicenseAssignmentResponse> getAssignmentsByUser(Integer userId);
    LicenseAssignmentResponse assignLicense(LicenseAssignmentRequest request);
    LicenseAssignmentResponse unassignLicense(Integer assignmentId);
    void deleteAssignment(Integer assignmentId);
}
