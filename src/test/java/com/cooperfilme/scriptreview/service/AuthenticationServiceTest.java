package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.repository.UserRepository;
import com.cooperfilme.scriptreview.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthenticationService authenticationService;

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("fail@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                authenticationService.loadUserByUsername("fail@test.com"));
    }
}

