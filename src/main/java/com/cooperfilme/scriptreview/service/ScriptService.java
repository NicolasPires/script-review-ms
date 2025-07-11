package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.dto.request.ScriptRequestDTO;
import com.cooperfilme.scriptreview.dto.response.ScriptResponseDTO;
import com.cooperfilme.scriptreview.entity.Script;
import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.exception.NotFoundException;
import com.cooperfilme.scriptreview.repository.ScriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScriptService {

    private final ScriptRepository scriptRepository;
    private final ScriptStatusFlowValidator validator;

    public ScriptResponseDTO submitScript(ScriptRequestDTO request) {
        Script script = Script.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .client(request.getClient())
                .status(ScriptStatus.AWAITING_ANALYSIS)
                .build();

        Script saved = scriptRepository.save(script);

        return new ScriptResponseDTO(saved.getId(), saved.getStatus());
    }

    public Optional<ScriptStatus> getScriptStatus(Long id) {
        return scriptRepository.findById(id)
                .map(Script::getStatus);
    }

    public void updateStatus(Long id, ScriptStatus newStatus) {
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Script not found"));

        validator.validate(script.getStatus(), newStatus);
        script.setStatus(newStatus);
        scriptRepository.save(script);
    }

}
