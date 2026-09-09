package com.licenseguard.repository;

import com.licenseguard.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    Optional<Vendor> findByVendorNameIgnoreCase(String vendorName);
    boolean existsByVendorNameIgnoreCase(String vendorName);
}
