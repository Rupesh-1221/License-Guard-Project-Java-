package com.licenseguard.service;

import com.licenseguard.dto.request.RenewalRequest;
import com.licenseguard.dto.response.RenewalResponse;

import java.util.List;

public interface RenewalService {
    List<RenewalResponse> getAllRenewals();
    RenewalResponse getRenewalById(Integer renewalId);
    List<RenewalResponse> getRenewalsByLicense(Integer licenseId);
    RenewalResponse renewLicense(RenewalRequest request);
    void deleteRenewal(Integer renewalId);
}
