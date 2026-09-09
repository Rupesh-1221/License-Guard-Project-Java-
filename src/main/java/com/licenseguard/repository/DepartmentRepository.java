package com.licenseguard.repository;

import com.licenseguard.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByDepartmentNameIgnoreCase(String departmentName);
    boolean existsByDepartmentNameIgnoreCase(String departmentName);
}
