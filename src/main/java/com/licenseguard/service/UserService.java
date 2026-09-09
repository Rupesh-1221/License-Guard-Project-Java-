package com.licenseguard.service;

import com.licenseguard.dto.request.UserRequest;
import com.licenseguard.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Integer userId);
    List<UserResponse> getUsersByDepartment(Integer departmentId);
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(Integer userId, UserRequest request);
    void deleteUser(Integer userId);
}
