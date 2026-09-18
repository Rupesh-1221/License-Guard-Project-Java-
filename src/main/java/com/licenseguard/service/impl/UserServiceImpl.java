package com.licenseguard.service.impl;

import com.licenseguard.dto.request.UserRequest;
import com.licenseguard.dto.response.UserResponse;
import com.licenseguard.entity.Department;
import com.licenseguard.entity.User;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.exception.ResourceNotFoundException;
import com.licenseguard.repository.DepartmentRepository;
import com.licenseguard.repository.UserRepository;
import com.licenseguard.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public UserServiceImpl(UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer userId) {
        User user = findUserEntityById(userId);
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByDepartment(Integer departmentId) {
        return userRepository.findByDepartmentDepartmentId(departmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with email '" + request.getEmail() + "' already exists");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        user.setDepartment(department);

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Override
    public UserResponse updateUser(Integer userId, UserRequest request) {
        User user = findUserEntityById(userId);

        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with email '" + request.getEmail() + "' already exists");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }
        user.setRole(request.getRole());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        user.setDepartment(department);

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = findUserEntityById(userId);
        
        if (user.getLicenseAssignments() != null && !user.getLicenseAssignments().isEmpty()) {
            throw new com.licenseguard.exception.BadRequestException("Cannot delete user because they have license assignments.");
        }
        
        if (user.getRenewals() != null && !user.getRenewals().isEmpty()) {
            throw new com.licenseguard.exception.BadRequestException("Cannot delete user because they are associated with license renewals.");
        }
        
        userRepository.delete(user);
    }

    private User findUserEntityById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private UserResponse mapToResponse(User user) {
        Integer departmentId = user.getDepartment() != null ? user.getDepartment().getDepartmentId() : null;
        String departmentName = user.getDepartment() != null ? user.getDepartment().getDepartmentName() : null;

        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                departmentId,
                departmentName
        );
    }
}
