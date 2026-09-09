package com.licenseguard.service;

import com.licenseguard.dto.request.DepartmentRequest;
import com.licenseguard.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse getDepartmentById(Integer departmentId);
    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(Integer departmentId, DepartmentRequest request);
    void deleteDepartment(Integer departmentId);
}
