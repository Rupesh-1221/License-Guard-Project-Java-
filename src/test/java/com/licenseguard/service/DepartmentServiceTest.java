package com.licenseguard.service;

import com.licenseguard.dto.request.DepartmentRequest;
import com.licenseguard.entity.Department;
import com.licenseguard.entity.User;
import com.licenseguard.exception.BadRequestException;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.repository.DepartmentRepository;
import com.licenseguard.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setDepartmentId(1);
        department.setDepartmentName("IT");
    }

    @Test
    void testCreateDepartment_DuplicateNameThrowsException() {
        DepartmentRequest request = new DepartmentRequest();
        request.setDepartmentName("IT");

        when(departmentRepository.existsByDepartmentNameIgnoreCase("IT")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> departmentService.createDepartment(request));
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void testDeleteDepartment_WithUsersThrowsException() {
        User user = new User();
        department.setUsers(List.of(user));

        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));

        assertThrows(BadRequestException.class, () -> departmentService.deleteDepartment(1));
        verify(departmentRepository, never()).delete(any());
    }

    @Test
    void testDeleteDepartment_WithoutUsersIsSuccessful() {
        department.setUsers(List.of());

        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));

        departmentService.deleteDepartment(1);

        verify(departmentRepository, times(1)).delete(department);
    }
}
