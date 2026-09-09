package com.licenseguard.service.impl;

import com.licenseguard.dto.request.RenewalRequest;
import com.licenseguard.dto.response.RenewalResponse;
import com.licenseguard.entity.License;
import com.licenseguard.entity.Renewal;
import com.licenseguard.entity.User;
import com.licenseguard.exception.BadRequestException;
import com.licenseguard.exception.ResourceNotFoundException;
import com.licenseguard.repository.LicenseRepository;
import com.licenseguard.repository.RenewalRepository;
import com.licenseguard.repository.UserRepository;
import com.licenseguard.service.RenewalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RenewalServiceImpl implements RenewalService {

    private final RenewalRepository renewalRepository;
    private final LicenseRepository licenseRepository;
    private final UserRepository userRepository;

    public RenewalServiceImpl(RenewalRepository renewalRepository,
                              LicenseRepository licenseRepository,
                              UserRepository userRepository) {
        this.renewalRepository = renewalRepository;
        this.licenseRepository = licenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RenewalResponse> getAllRenewals() {
        return renewalRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RenewalResponse getRenewalById(Integer renewalId) {
        Renewal renewal = findRenewalEntityById(renewalId);
        return mapToResponse(renewal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RenewalResponse> getRenewalsByLicense(Integer licenseId) {
        return renewalRepository.findByLicenseLicenseId(licenseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RenewalResponse renewLicense(RenewalRequest request) {
        License license = licenseRepository.findById(request.getLicenseId())
                .orElseThrow(() -> new ResourceNotFoundException("License not found with id: " + request.getLicenseId()));

        LocalDate oldExpiryDate = license.getExpiryDate();
        if (request.getNewExpiryDate() != null && !request.getNewExpiryDate().isAfter(oldExpiryDate)) {
            throw new BadRequestException("New expiry date (" + request.getNewExpiryDate() + ") must be after current expiry date (" + oldExpiryDate + ")");
        }

        User renewedByUser = null;
        if (request.getRenewedBy() != null) {
            renewedByUser = userRepository.findById(request.getRenewedBy())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getRenewedBy()));
        }

        Renewal renewal = new Renewal();
        renewal.setLicense(license);
        renewal.setOldExpiryDate(oldExpiryDate);
        renewal.setNewExpiryDate(request.getNewExpiryDate());
        renewal.setRenewalDate(request.getRenewalDate() != null ? request.getRenewalDate() : LocalDate.now());
        renewal.setRenewalCost(request.getRenewalCost());
        renewal.setRenewedByUser(renewedByUser);
        renewal.setRemarks(request.getRemarks());

        license.setExpiryDate(request.getNewExpiryDate());
        license.setStatus("ACTIVE");
        licenseRepository.save(license);

        Renewal saved = renewalRepository.save(renewal);
        return mapToResponse(saved);
    }

    @Override
    public void deleteRenewal(Integer renewalId) {
        Renewal renewal = findRenewalEntityById(renewalId);
        renewalRepository.delete(renewal);
    }

    private Renewal findRenewalEntityById(Integer renewalId) {
        return renewalRepository.findById(renewalId)
                .orElseThrow(() -> new ResourceNotFoundException("Renewal not found with id: " + renewalId));
    }

    private RenewalResponse mapToResponse(Renewal renewal) {
        Integer licenseId = renewal.getLicense() != null ? renewal.getLicense().getLicenseId() : null;
        String licenseKey = renewal.getLicense() != null ? renewal.getLicense().getLicenseKey() : null;
        String softwareName = null;
        if (renewal.getLicense() != null && renewal.getLicense().getSoftware() != null) {
            softwareName = renewal.getLicense().getSoftware().getSoftwareName();
        }

        Integer renewedByUserId = renewal.getRenewedByUser() != null ? renewal.getRenewedByUser().getUserId() : null;
        String renewedByUserName = renewal.getRenewedByUser() != null ? renewal.getRenewedByUser().getName() : null;

        return new RenewalResponse(
                renewal.getRenewalId(),
                licenseId,
                licenseKey,
                softwareName,
                renewal.getOldExpiryDate(),
                renewal.getNewExpiryDate(),
                renewal.getRenewalDate(),
                renewal.getRenewalCost(),
                renewedByUserId,
                renewedByUserName,
                renewal.getRemarks()
        );
    }
}
