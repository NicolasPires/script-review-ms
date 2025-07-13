package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.dto.request.ScriptRequestDTO;
import com.cooperfilme.scriptreview.dto.response.ScriptResponseDTO;
import com.cooperfilme.scriptreview.entity.Script;
import com.cooperfilme.scriptreview.entity.User;
import com.cooperfilme.scriptreview.entity.Vote;
import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.exception.BusinessException;
import com.cooperfilme.scriptreview.exception.NotFoundException;
import com.cooperfilme.scriptreview.repository.ScriptRepository;
import com.cooperfilme.scriptreview.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScriptService {

    private final ScriptRepository scriptRepository;
    private final ScriptStatusFlowValidator validator;
    private final VoteRepository voteRepository;

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

    public void vote(Long scriptId, User approver, boolean approved) {
        Script script = scriptRepository.findById(scriptId)
                .orElseThrow(() -> new NotFoundException("Script not found"));

        if (script.getStatus() != ScriptStatus.AWAITING_APPROVAL && script.getStatus() != ScriptStatus.IN_APPROVAL) {
            throw new BusinessException("Voting not allowed at this stage");
        }

        if (voteRepository.existsByScriptAndApprover(script, approver)) {
            throw new BusinessException("Approver already voted");
        }

        Vote vote = Vote.builder()
                .script(script)
                .approver(approver)
                .approved(approved)
                .votedAt(LocalDateTime.now())
                .build();

        voteRepository.save(vote);

        List<Vote> votes = voteRepository.findByScript(script);

        // Primeiro voto → mudar para IN_APPROVAL
        if (script.getStatus() == ScriptStatus.AWAITING_APPROVAL) {
            script.setStatus(ScriptStatus.IN_APPROVAL);
        }

        // Se algum voto for NÃO, muda para REJECTED
        if (votes.stream().anyMatch(v -> !v.isApproved())) {
            script.setStatus(ScriptStatus.REJECTED);
        }

        // Se todos os 3 forem SIM, muda para APPROVED
        if (votes.size() == 3 && votes.stream().allMatch(Vote::isApproved)) {
            script.setStatus(ScriptStatus.APPROVED);
        }

        scriptRepository.save(script);
    }

    public List<ScriptResponseDTO> getByClientEmail(String email) {
        return scriptRepository.findByClientEmail(email)
                .stream()
                .map(script -> new ScriptResponseDTO(script.getId(), script.getStatus()))
                .toList();
    }

}
