package com.licenseguard.repository;

import com.licenseguard.entity.Software;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoftwareRepository extends JpaRepository<Software, Integer> {
    List<Software> findByVendorVendorId(Integer vendorId);
    List<Software> findBySoftwareNameContainingIgnoreCase(String softwareName);
}
