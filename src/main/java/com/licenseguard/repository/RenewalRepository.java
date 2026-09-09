package com.licenseguard.repository;

import com.licenseguard.entity.Renewal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RenewalRepository extends JpaRepository<Renewal, Integer> {
    List<Renewal> findByLicenseLicenseId(Integer licenseId);
    List<Renewal> findByRenewedByUserUserId(Integer userId);
}
