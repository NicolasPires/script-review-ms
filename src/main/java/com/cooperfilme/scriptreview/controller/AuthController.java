package com.cooperfilme.scriptreview.controller;

import com.cooperfilme.scriptreview.dto.request.AuthRequestDTO;
import com.cooperfilme.scriptreview.dto.response.AuthResponseDTO;
import com.cooperfilme.scriptreview.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
