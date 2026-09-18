package com.licenseguard.service.impl;

import com.licenseguard.dto.request.LicenseAssignmentRequest;
import com.licenseguard.dto.response.LicenseAssignmentResponse;
import com.licenseguard.entity.License;
import com.licenseguard.entity.LicenseAssignment;
import com.licenseguard.entity.User;
import com.licenseguard.exception.BadRequestException;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.exception.InsufficientSeatsException;
import com.licenseguard.exception.ResourceNotFoundException;
import com.licenseguard.repository.LicenseAssignmentRepository;
import com.licenseguard.repository.LicenseRepository;
import com.licenseguard.repository.UserRepository;
import com.licenseguard.service.LicenseAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LicenseAssignmentServiceImpl implements LicenseAssignmentService {

    private final LicenseAssignmentRepository assignmentRepository;
    private final LicenseRepository licenseRepository;
    private final UserRepository userRepository;

    public LicenseAssignmentServiceImpl(LicenseAssignmentRepository assignmentRepository,
                                       LicenseRepository licenseRepository,
                                       UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.licenseRepository = licenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseAssignmentResponse> getAllAssignments() {
        return assignmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LicenseAssignmentResponse getAssignmentById(Integer assignmentId) {
        LicenseAssignment assignment = findAssignmentEntityById(assignmentId);
        return mapToResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseAssignmentResponse> getAssignmentsByLicense(Integer licenseId) {
        return assignmentRepository.findByLicenseLicenseId(licenseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseAssignmentResponse> getAssignmentsByUser(Integer userId) {
        return assignmentRepository.findByUserUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LicenseAssignmentResponse assignLicense(LicenseAssignmentRequest request) {
        License license = licenseRepository.findById(request.getLicenseId())
                .orElseThrow(() -> new ResourceNotFoundException("License not found with id: " + request.getLicenseId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        assignmentRepository.findByLicenseLicenseIdAndUserUserIdAndStatus(license.getLicenseId(), user.getUserId(), "ACTIVE")
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Active license assignment already exists for user '" + user.getName() + "' and this license");
                });

        if (license.getAvailableSeats() <= 0) {
            throw new InsufficientSeatsException("No available seats left for license key: " + license.getLicenseKey());
        }

        license.setAvailableSeats(license.getAvailableSeats() - 1);
        licenseRepository.save(license);

        LicenseAssignment assignment = new LicenseAssignment();
        assignment.setLicense(license);
        assignment.setUser(user);
        assignment.setAssignedDate(request.getAssignedDate() != null ? request.getAssignedDate() : LocalDate.now());
        assignment.setStatus("ACTIVE");

        LicenseAssignment saved = assignmentRepository.save(assignment);
        return mapToResponse(saved);
    }

    @Override
    public LicenseAssignmentResponse unassignLicense(Integer assignmentId) {
        LicenseAssignment assignment = findAssignmentEntityById(assignmentId);

        if ("INACTIVE".equalsIgnoreCase(assignment.getStatus())) {
            throw new BadRequestException("License assignment is already unassigned (INACTIVE)");
        }

        assignment.setStatus("INACTIVE");
        assignment.setUnassignedDate(LocalDate.now());

        License license = assignment.getLicense();
        if (license != null) {
            if (license.getAvailableSeats() < license.getTotalSeats()) {
                license.setAvailableSeats(license.getAvailableSeats() + 1);
                licenseRepository.save(license);
            }
        }

        LicenseAssignment updated = assignmentRepository.save(assignment);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAssignment(Integer assignmentId) {
        LicenseAssignment assignment = findAssignmentEntityById(assignmentId);
        if ("ACTIVE".equalsIgnoreCase(assignment.getStatus())) {
            throw new com.licenseguard.exception.BadRequestException("Cannot delete an active assignment. Please unassign the license first.");
        }
        assignmentRepository.delete(assignment);
    }

    private LicenseAssignment findAssignmentEntityById(Integer assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("License assignment not found with id: " + assignmentId));
    }

    private LicenseAssignmentResponse mapToResponse(LicenseAssignment assignment) {
        Integer licenseId = assignment.getLicense() != null ? assignment.getLicense().getLicenseId() : null;
        String licenseKey = assignment.getLicense() != null ? assignment.getLicense().getLicenseKey() : null;
        String softwareName = null;
        if (assignment.getLicense() != null && assignment.getLicense().getSoftware() != null) {
            softwareName = assignment.getLicense().getSoftware().getSoftwareName();
        }

        Integer userId = assignment.getUser() != null ? assignment.getUser().getUserId() : null;
        String userName = assignment.getUser() != null ? assignment.getUser().getName() : null;
        String userEmail = assignment.getUser() != null ? assignment.getUser().getEmail() : null;

        return new LicenseAssignmentResponse(
                assignment.getAssignmentId(),
                licenseId,
                licenseKey,
                softwareName,
                userId,
                userName,
                userEmail,
                assignment.getAssignedDate(),
                assignment.getUnassignedDate(),
                assignment.getStatus()
        );
    }
}
