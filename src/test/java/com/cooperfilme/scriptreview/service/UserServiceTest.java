package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.entity.User;
import com.cooperfilme.scriptreview.exception.NotFoundException;
import com.cooperfilme.scriptreview.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void shouldReturnAuthenticatedUserSuccessfully() {
        // Arrange
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("user@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        User user = new User();
        user.setEmail("user@example.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null)
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Act
        User result = userService.getAuthenticatedUser();

        // Assert
        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // Arrange
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("notfound@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null)
        );

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.getAuthenticatedUser());
    }

    @Test
    void shouldThrowWhenPrincipalIsNotUserDetails() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("someString", null)
        );

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.getAuthenticatedUser());
    }
}
