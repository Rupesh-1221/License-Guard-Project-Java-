package com.licenseguard.service.impl;

import com.licenseguard.dto.request.DepartmentRequest;
import com.licenseguard.dto.response.DepartmentResponse;
import com.licenseguard.entity.Department;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.exception.ResourceNotFoundException;
import com.licenseguard.repository.DepartmentRepository;
import com.licenseguard.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Integer departmentId) {
        Department department = findDepartmentEntityById(departmentId);
        return mapToResponse(department);
    }

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByDepartmentNameIgnoreCase(request.getDepartmentName())) {
            throw new DuplicateResourceException("Department with name '" + request.getDepartmentName() + "' already exists");
        }

        Department department = new Department();
        department.setDepartmentName(request.getDepartmentName());
        department.setDescription(request.getDescription());

        Department saved = departmentRepository.save(department);
        return mapToResponse(saved);
    }

    @Override
    public DepartmentResponse updateDepartment(Integer departmentId, DepartmentRequest request) {
        Department department = findDepartmentEntityById(departmentId);

        if (!department.getDepartmentName().equalsIgnoreCase(request.getDepartmentName()) &&
                departmentRepository.existsByDepartmentNameIgnoreCase(request.getDepartmentName())) {
            throw new DuplicateResourceException("Department with name '" + request.getDepartmentName() + "' already exists");
        }

        department.setDepartmentName(request.getDepartmentName());
        department.setDescription(request.getDescription());

        Department updated = departmentRepository.save(department);
        return mapToResponse(updated);
    }

    @Override
    public void deleteDepartment(Integer departmentId) {
        Department department = findDepartmentEntityById(departmentId);
        departmentRepository.delete(department);
    }

    private Department findDepartmentEntityById(Integer departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
    }

    private DepartmentResponse mapToResponse(Department department) {
        int userCount = department.getUsers() != null ? department.getUsers().size() : 0;
        return new DepartmentResponse(
                department.getDepartmentId(),
                department.getDepartmentName(),
                department.getDescription(),
                userCount
        );
    }
}
