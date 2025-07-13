package com.cooperfilme.scriptreview.controller;

import com.cooperfilme.scriptreview.dto.request.ScriptRequestDTO;
import com.cooperfilme.scriptreview.dto.request.UpdateStatusRequest;
import com.cooperfilme.scriptreview.dto.request.VoteRequestDTO;
import com.cooperfilme.scriptreview.dto.response.ScriptResponseDTO;
import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.service.ScriptService;
import com.cooperfilme.scriptreview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/scripts")
@RequiredArgsConstructor
public class ScriptController {

    private final ScriptService scriptService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ScriptResponseDTO> submitScript(@RequestBody ScriptRequestDTO request) {
        ScriptResponseDTO response = scriptService.submitScript(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getStatus(@PathVariable Long id) {
        Optional<ScriptStatus> status = scriptService.getScriptStatus(id);
        return status.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Script not found")));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        ScriptStatus newStatus = ScriptStatus.valueOf(request.getNewStatus());
        scriptService.updateStatus(id, newStatus);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<Void> vote(@PathVariable Long id,
                                     @RequestBody VoteRequestDTO request,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        scriptService.vote(id, userService.getAuthenticatedUser(), request.isApproved());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{email}")
    public ResponseEntity<List<ScriptResponseDTO>> getByClientEmail(@PathVariable String email) {
        List<ScriptResponseDTO> scripts = scriptService.getByClientEmail(email);
        return ResponseEntity.ok(scripts);
    }
}
