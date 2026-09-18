package com.licenseguard.service;

import com.licenseguard.dto.request.UserRequest;
import com.licenseguard.entity.LicenseAssignment;
import com.licenseguard.entity.Renewal;
import com.licenseguard.entity.User;
import com.licenseguard.exception.BadRequestException;
import com.licenseguard.exception.DuplicateResourceException;
import com.licenseguard.repository.UserRepository;
import com.licenseguard.service.impl.UserServiceImpl;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
    }

    @Test
    void testCreateUser_DuplicateEmailThrowsException() {
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_WithAssignmentsThrowsException() {
        LicenseAssignment assignment = new LicenseAssignment();
        user.setLicenseAssignments(List.of(assignment));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.deleteUser(1));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testDeleteUser_WithRenewalsThrowsException() {
        Renewal renewal = new Renewal();
        user.setRenewals(List.of(renewal));
        user.setLicenseAssignments(List.of());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.deleteUser(1));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testDeleteUser_WithoutAssociationsIsSuccessful() {
        user.setLicenseAssignments(List.of());
        user.setRenewals(List.of());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userRepository, times(1)).delete(user);
    }
}
