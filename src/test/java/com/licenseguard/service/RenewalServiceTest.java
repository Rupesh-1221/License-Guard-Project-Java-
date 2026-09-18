package com.licenseguard.service;

import com.licenseguard.dto.request.RenewalRequest;
import com.licenseguard.entity.License;
import com.licenseguard.exception.BadRequestException;
import com.licenseguard.repository.LicenseRepository;
import com.licenseguard.repository.RenewalRepository;
import com.licenseguard.repository.UserRepository;
import com.licenseguard.service.impl.RenewalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenewalServiceTest {

    @Mock
    private RenewalRepository renewalRepository;

    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RenewalServiceImpl renewalService;

    private License license;

    @BeforeEach
    void setUp() {
        license = new License();
        license.setLicenseId(1);
        license.setExpiryDate(LocalDate.of(2023, 12, 31));
    }

    @Test
    void testRenewLicense_NewExpiryDateBeforeOldThrowsException() {
        RenewalRequest request = new RenewalRequest();
        request.setLicenseId(1);
        request.setNewExpiryDate(LocalDate.of(2023, 12, 30)); // Before old expiry date

        when(licenseRepository.findById(1)).thenReturn(Optional.of(license));

        assertThrows(BadRequestException.class, () -> renewalService.renewLicense(request));
        verify(renewalRepository, never()).save(any());
    }

    @Test
    void testRenewLicense_NewExpiryDateSameAsOldThrowsException() {
        RenewalRequest request = new RenewalRequest();
        request.setLicenseId(1);
        request.setNewExpiryDate(LocalDate.of(2023, 12, 31)); // Same as old expiry date

        when(licenseRepository.findById(1)).thenReturn(Optional.of(license));

        assertThrows(BadRequestException.class, () -> renewalService.renewLicense(request));
        verify(renewalRepository, never()).save(any());
    }
}
