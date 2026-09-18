package com.licenseguard.service;

import com.licenseguard.dto.request.VendorRequest;
import com.licenseguard.entity.Software;
import com.licenseguard.entity.Vendor;
import com.licenseguard.exception.BadRequestException;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.repository.VendorRepository;
import com.licenseguard.service.impl.VendorServiceImpl;
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
class VendorServiceTest {

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorServiceImpl vendorService;

    private Vendor vendor;

    @BeforeEach
    void setUp() {
        vendor = new Vendor();
        vendor.setVendorId(1);
        vendor.setVendorName("Microsoft");
    }

    @Test
    void testCreateVendor_DuplicateNameThrowsException() {
        VendorRequest request = new VendorRequest();
        request.setVendorName("Microsoft");

        when(vendorRepository.existsByVendorNameIgnoreCase("Microsoft")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> vendorService.createVendor(request));
        verify(vendorRepository, never()).save(any());
    }

    @Test
    void testDeleteVendor_WithSoftwareThrowsException() {
        Software software = new Software();
        vendor.setSoftwareList(List.of(software));

        when(vendorRepository.findById(1)).thenReturn(Optional.of(vendor));

        assertThrows(BadRequestException.class, () -> vendorService.deleteVendor(1));
        verify(vendorRepository, never()).delete(any());
    }

    @Test
    void testDeleteVendor_WithoutSoftwareIsSuccessful() {
        vendor.setSoftwareList(List.of());

        when(vendorRepository.findById(1)).thenReturn(Optional.of(vendor));

        vendorService.deleteVendor(1);

        verify(vendorRepository, times(1)).delete(vendor);
    }
}
