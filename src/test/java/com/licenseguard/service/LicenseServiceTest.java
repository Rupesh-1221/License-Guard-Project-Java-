package com.licenseguard.service;

import com.licenseguard.dto.request.LicenseRequest;
import com.licenseguard.entity.License;
import com.licenseguard.entity.LicenseAssignment;
import com.licenseguard.entity.Renewal;
import com.licenseguard.entity.Software;
import com.licenseguard.exception.BadRequestException;
import com.licenseguard.repository.LicenseRepository;
import com.licenseguard.repository.SoftwareRepository;
import com.licenseguard.service.impl.LicenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenseServiceTest {

    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private SoftwareRepository softwareRepository;

    @InjectMocks
    private LicenseServiceImpl licenseService;

    private License license;
    private Software software;

    @BeforeEach
    void setUp() {
        software = new Software();
        software.setSoftwareId(1);

        license = new License();
        license.setLicenseId(1);
        license.setLicenseKey("KEY-123");
        license.setSoftware(software);
    }

    @Test
    void testCreateLicense_StartDateBeforePurchaseDateThrowsException() {
        LicenseRequest request = new LicenseRequest();
        request.setSoftwareId(1);
        request.setPurchaseDate(LocalDate.of(2023, 1, 15));
        request.setStartDate(LocalDate.of(2023, 1, 10)); // Before purchase date

        when(softwareRepository.findById(1)).thenReturn(Optional.of(software));

        assertThrows(BadRequestException.class, () -> licenseService.createLicense(request));
        verify(licenseRepository, never()).save(any());
    }

    @Test
    void testCreateLicense_ExpiryDateBeforeStartDateThrowsException() {
        LicenseRequest request = new LicenseRequest();
        request.setSoftwareId(1);
        request.setPurchaseDate(LocalDate.of(2023, 1, 1));
        request.setStartDate(LocalDate.of(2023, 1, 10));
        request.setExpiryDate(LocalDate.of(2023, 1, 5)); // Before start date

        when(softwareRepository.findById(1)).thenReturn(Optional.of(software));

        assertThrows(BadRequestException.class, () -> licenseService.createLicense(request));
        verify(licenseRepository, never()).save(any());
    }

    @Test
    void testDeleteLicense_WithAssignmentsThrowsException() {
        LicenseAssignment assignment = new LicenseAssignment();
        license.setLicenseAssignments(List.of(assignment));

        when(licenseRepository.findById(1)).thenReturn(Optional.of(license));

        assertThrows(BadRequestException.class, () -> licenseService.deleteLicense(1));
        verify(licenseRepository, never()).delete(any());
    }

    @Test
    void testDeleteLicense_WithRenewalsThrowsException() {
        Renewal renewal = new Renewal();
        license.setRenewals(List.of(renewal));
        license.setLicenseAssignments(List.of());

        when(licenseRepository.findById(1)).thenReturn(Optional.of(license));

        assertThrows(BadRequestException.class, () -> licenseService.deleteLicense(1));
        verify(licenseRepository, never()).delete(any());
    }

    @Test
    void testDeleteLicense_WithoutAssociationsIsSuccessful() {
        license.setLicenseAssignments(List.of());
        license.setRenewals(List.of());

        when(licenseRepository.findById(1)).thenReturn(Optional.of(license));

        licenseService.deleteLicense(1);

        verify(licenseRepository, times(1)).delete(license);
    }
}
