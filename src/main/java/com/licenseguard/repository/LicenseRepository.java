package com.licenseguard.repository;

import com.licenseguard.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Integer> {
    Optional<License> findByLicenseKey(String licenseKey);
    boolean existsByLicenseKey(String licenseKey);
    List<License> findBySoftwareSoftwareId(Integer softwareId);
    List<License> findByStatus(String status);
    List<License> findByExpiryDateBefore(LocalDate date);
    List<License> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
}
