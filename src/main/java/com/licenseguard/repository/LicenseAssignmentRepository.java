package com.licenseguard.repository;

import com.licenseguard.entity.LicenseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseAssignmentRepository extends JpaRepository<LicenseAssignment, Integer> {
    List<LicenseAssignment> findByLicenseLicenseId(Integer licenseId);
    List<LicenseAssignment> findByUserUserId(Integer userId);
    List<LicenseAssignment> findByStatus(String status);
    Optional<LicenseAssignment> findByLicenseLicenseIdAndUserUserIdAndStatus(Integer licenseId, Integer userId, String status);
}
