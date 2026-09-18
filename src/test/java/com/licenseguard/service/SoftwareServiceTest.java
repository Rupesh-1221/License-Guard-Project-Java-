package com.licenseguard.service;

import com.licenseguard.dto.request.SoftwareRequest;
import com.licenseguard.entity.License;
import com.licenseguard.entity.Software;
import com.licenseguard.entity.Vendor;
import com.licenseguard.exception.BadRequestException;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.repository.SoftwareRepository;
import com.licenseguard.repository.VendorRepository;
import com.licenseguard.service.impl.SoftwareServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftwareServiceTest {

    @Mock
    private SoftwareRepository softwareRepository;

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private SoftwareServiceImpl softwareService;

    private Software software;
    private Vendor vendor;

    @BeforeEach
    void setUp() {
        vendor = new Vendor();
        vendor.setVendorId(1);

        software = new Software();
        software.setSoftwareId(1);
        software.setSoftwareName("Windows");
        software.setVersion("11");
        software.setVendor(vendor);
    }

    @Test
    void testCreateSoftware_DuplicateNameAndVersionThrowsException() {
        SoftwareRequest request = new SoftwareRequest();
        request.setSoftwareName("Windows");
        request.setVersion("11");
        request.setVendorId(1);

        when(vendorRepository.findById(1)).thenReturn(Optional.of(vendor));
        when(softwareRepository.existsBySoftwareNameIgnoreCaseAndVersionAndVendorVendorId("Windows", "11", 1)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> softwareService.createSoftware(request));
        verify(softwareRepository, never()).save(any());
    }

    @Test
    void testDeleteSoftware_WithLicensesThrowsException() {
        License license = new License();
        software.setLicenses(List.of(license));

        when(softwareRepository.findById(1)).thenReturn(Optional.of(software));

        assertThrows(BadRequestException.class, () -> softwareService.deleteSoftware(1));
        verify(softwareRepository, never()).delete(any());
    }

    @Test
    void testDeleteSoftware_WithoutLicensesIsSuccessful() {
        software.setLicenses(List.of());

        when(softwareRepository.findById(1)).thenReturn(Optional.of(software));

        softwareService.deleteSoftware(1);

        verify(softwareRepository, times(1)).delete(software);
    }
}
