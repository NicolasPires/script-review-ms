package com.cooperfilme.scriptreview.controller;

import com.cooperfilme.scriptreview.dto.request.ScriptRequestDTO;
import com.cooperfilme.scriptreview.dto.response.ScriptResponseDTO;
import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.service.ScriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
