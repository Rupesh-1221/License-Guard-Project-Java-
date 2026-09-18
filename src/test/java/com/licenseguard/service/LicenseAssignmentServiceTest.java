package com.licenseguard.service;

import com.licenseguard.dto.request.LicenseAssignmentRequest;
import com.licenseguard.entity.License;
import com.licenseguard.entity.LicenseAssignment;
import com.licenseguard.entity.User;
import com.licenseguard.exception.BadRequestException;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.exception.InsufficientSeatsException;
import com.licenseguard.repository.LicenseAssignmentRepository;
import com.licenseguard.repository.LicenseRepository;
import com.licenseguard.repository.UserRepository;
import com.licenseguard.service.impl.LicenseAssignmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenseAssignmentServiceTest {

    @Mock
    private LicenseAssignmentRepository assignmentRepository;

    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LicenseAssignmentServiceImpl assignmentService;

    private License license;
    private User user;
    private LicenseAssignment assignment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);

        license = new License();
        license.setLicenseId(1);
        license.setLicenseKey("KEY-123");
        license.setTotalSeats(10);
        license.setAvailableSeats(10);

        assignment = new LicenseAssignment();
        assignment.setAssignmentId(1);
        assignment.setLicense(license);
        assignment.setUser(user);
        assignment.setStatus("ACTIVE");
    }

    @Test
    void testAssignLicense_DuplicateActiveAssignmentThrowsException() {
        LicenseAssignmentRequest request = new LicenseAssignmentRequest();
        request.setLicenseId(1);
        request.setUserId(1);

        when(licenseRepository.findById(1)).thenReturn(Optional.of(license));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(assignmentRepository.findByLicenseLicenseIdAndUserUserIdAndStatus(1, 1, "ACTIVE"))
                .thenReturn(Optional.of(assignment));

        assertThrows(DuplicateResourceException.class, () -> assignmentService.assignLicense(request));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void testAssignLicense_InsufficientSeatsThrowsException() {
        LicenseAssignmentRequest request = new LicenseAssignmentRequest();
        request.setLicenseId(1);
        request.setUserId(1);

        license.setAvailableSeats(0);

        when(licenseRepository.findById(1)).thenReturn(Optional.of(license));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(assignmentRepository.findByLicenseLicenseIdAndUserUserIdAndStatus(1, 1, "ACTIVE"))
                .thenReturn(Optional.empty());

        assertThrows(InsufficientSeatsException.class, () -> assignmentService.assignLicense(request));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void testDeleteAssignment_ActiveAssignmentThrowsException() {
        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));

        assertThrows(BadRequestException.class, () -> assignmentService.deleteAssignment(1));
        verify(assignmentRepository, never()).delete(any());
    }

    @Test
    void testDeleteAssignment_InactiveAssignmentIsSuccessful() {
        assignment.setStatus("INACTIVE");

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));

        assignmentService.deleteAssignment(1);

        verify(assignmentRepository, times(1)).delete(assignment);
    }
}
