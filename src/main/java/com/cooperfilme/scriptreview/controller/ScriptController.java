package com.cooperfilme.scriptreview.controller;

import com.cooperfilme.scriptreview.dto.request.ScriptRequestDTO;
import com.cooperfilme.scriptreview.dto.request.VoteRequestDTO;
import com.cooperfilme.scriptreview.dto.response.ScriptResponseDTO;
import com.cooperfilme.scriptreview.entity.User;
import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.service.ScriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/scripts")
@RequiredArgsConstructor
public class ScriptController {

    private final ScriptService scriptService;

    @PostMapping
    public ResponseEntity<ScriptResponseDTO> submitScript(@RequestBody ScriptRequestDTO request) {
        ScriptResponseDTO response = scriptService.submitScript(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ScriptStatus> getScriptStatus(@PathVariable Long id) {
        return scriptService.getScriptStatus(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        ScriptStatus newStatus = ScriptStatus.valueOf(body.get("status"));
        scriptService.updateStatus(id, newStatus);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('APPROVER')")
    @PostMapping("/{id}/vote")
    public ResponseEntity<Void> vote(
            @PathVariable Long id,
            @RequestBody VoteRequestDTO dto,
            @AuthenticationPrincipal User approver) {
        scriptService.vote(id, approver, dto.isApproved());
        return ResponseEntity.noContent().build();
    }
}
