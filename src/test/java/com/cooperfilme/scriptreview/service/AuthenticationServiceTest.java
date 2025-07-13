package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.dto.request.AuthRequestDTO;
import com.cooperfilme.scriptreview.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticationConfiguration authenticationConfiguration;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    Authentication authentication;

    @InjectMocks
    AuthenticationService authenticationService;

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("fail@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                authenticationService.loadUserByUsername("fail@test.com"));
    }

    @Test
    void shouldAuthenticateSuccessfully() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("analyst@cooperfilme.com", "123456");

        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        Authentication result = authenticationService.authenticate(request);

        assertNotNull(result);
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void shouldThrowBadCredentialsWhenAuthenticationFails() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("analyst@cooperfilme.com", "wrong");

        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("E-mail ou senha inválidos"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUnexpectedErrorOccurs() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("analyst@cooperfilme.com", "123456");

        when(authenticationConfiguration.getAuthenticationManager())
                .thenThrow(new IllegalStateException("Falha inesperada"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));

        assertEquals("Falha inesperada", ex.getCause().getMessage());
    }

}
