package com.cooperfilme.scriptreview.controller;

import com.cooperfilme.scriptreview.dto.request.AuthRequestDTO;
import com.cooperfilme.scriptreview.dto.response.AuthResponseDTO;
import com.cooperfilme.scriptreview.security.JwtTokenProvider;
import com.cooperfilme.scriptreview.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        Authentication authentication = authenticationService.authenticate(request);
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
