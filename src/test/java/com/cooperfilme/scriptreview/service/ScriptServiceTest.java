package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.dto.request.ScriptRequestDTO;
import com.cooperfilme.scriptreview.dto.response.ScriptResponseDTO;
import com.cooperfilme.scriptreview.entity.Client;
import com.cooperfilme.scriptreview.entity.Script;
import com.cooperfilme.scriptreview.entity.User;
import com.cooperfilme.scriptreview.entity.Vote;
import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.exception.BusinessException;
import com.cooperfilme.scriptreview.exception.NotFoundException;
import com.cooperfilme.scriptreview.repository.ScriptRepository;
import com.cooperfilme.scriptreview.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScriptServiceTest {

    @Mock
    ScriptRepository scriptRepository;
    @Mock
    VoteRepository voteRepository;
    @Mock
    ScriptStatusFlowValidator validator;

    @InjectMocks
    ScriptService scriptService;

    @Test
    void shouldSubmitScriptSuccessfully() {
        ScriptRequestDTO request = new ScriptRequestDTO("Title", "Content", new Client("Client", "c@email.com", "123"));
        Script script = Script.builder().id(1L).status(ScriptStatus.AWAITING_ANALYSIS).build();

        when(scriptRepository.save(any())).thenReturn(script);

        ScriptResponseDTO response = scriptService.submitScript(request);

        assertEquals(1L, response.getId());
        assertEquals(ScriptStatus.AWAITING_ANALYSIS, response.getStatus());
        verify(scriptRepository).save(any());
    }

    @Test
    void shouldReturnScriptStatusIfExists() {
        Script script = new Script();
        script.setStatus(ScriptStatus.IN_REVIEW);

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script));

        Optional<ScriptStatus> result = scriptService.getScriptStatus(1L);

        assertTrue(result.isPresent());
        assertEquals(ScriptStatus.IN_REVIEW, result.get());
    }

    @Test
    void shouldUpdateStatusSuccessfully() {
        Script script = new Script();
        script.setId(1L);
        script.setStatus(ScriptStatus.IN_ANALYSIS);

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script));
        scriptService.updateStatus(1L, ScriptStatus.AWAITING_REVIEW);

        verify(validator).validate(ScriptStatus.IN_ANALYSIS, ScriptStatus.AWAITING_REVIEW);
        verify(scriptRepository).save(script);
    }

    @Test
    void shouldThrowWhenScriptNotFoundInUpdateStatus() {
        when(scriptRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                scriptService.updateStatus(99L, ScriptStatus.APPROVED));
    }

    @Test
    void shouldVoteAndChangeToInApprovalOnFirstVote() {
        Script script = new Script();
        script.setStatus(ScriptStatus.AWAITING_APPROVAL);
        User approver = new User();
        Vote vote = Vote.builder().approved(true).build();

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script));
        when(voteRepository.existsByScriptAndApprover(script, approver)).thenReturn(false);
        when(voteRepository.findByScript(script)).thenReturn(List.of(vote));

        scriptService.vote(1L, approver, true);

        assertEquals(ScriptStatus.IN_APPROVAL, script.getStatus());
        verify(voteRepository).save(any());
        verify(scriptRepository).save(script);
    }

    @Test
    void shouldRejectScriptIfAnyVoteIsNo() {
        Script script = new Script();
        script.setStatus(ScriptStatus.IN_APPROVAL);
        User approver = new User();
        Vote noVote = Vote.builder().approved(false).build();

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script));
        when(voteRepository.existsByScriptAndApprover(script, approver)).thenReturn(false);
        when(voteRepository.findByScript(script)).thenReturn(List.of(noVote));

        scriptService.vote(1L, approver, false);

        assertEquals(ScriptStatus.REJECTED, script.getStatus());
    }

    @Test
    void shouldApproveScriptIfThreeYesVotes() {
        Script script = new Script();
        script.setStatus(ScriptStatus.IN_APPROVAL);
        User approver = new User();
        Vote v1 = Vote.builder().approved(true).build();
        Vote v2 = Vote.builder().approved(true).build();
        Vote v3 = Vote.builder().approved(true).build();

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script));
        when(voteRepository.existsByScriptAndApprover(script, approver)).thenReturn(false);
        when(voteRepository.findByScript(script)).thenReturn(List.of(v1, v2, v3));

        scriptService.vote(1L, approver, true);

        assertEquals(ScriptStatus.APPROVED, script.getStatus());
    }

    @Test
    void shouldThrowWhenScriptNotFoundInVote() {
        when(scriptRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> scriptService.vote(1L, new User(), true));
    }

    @Test
    void shouldThrowWhenVotingNotAllowed() {
        Script script = new Script();
        script.setStatus(ScriptStatus.IN_REVIEW);

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script));

        assertThrows(BusinessException.class, () -> scriptService.vote(1L, new User(), true));
    }

    @Test
    void shouldThrowWhenUserAlreadyVoted() {
        Script script = new Script();
        script.setStatus(ScriptStatus.AWAITING_APPROVAL);
        User approver = new User();

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script));
        when(voteRepository.existsByScriptAndApprover(script, approver)).thenReturn(true);

        assertThrows(BusinessException.class, () -> scriptService.vote(1L, approver, true));
    }

    @Test
    void shouldNotApproveScriptIfNotAllVotesAreYesEvenWithThreeVotes() {
        Script script = new Script();
        script.setStatus(ScriptStatus.IN_APPROVAL);
        User approver = new User();

        Vote v1 = Vote.builder().approved(true).build();
        Vote v2 = Vote.builder().approved(true).build();
        Vote v3 = Vote.builder().approved(false).build(); // um voto negativo

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script));
        when(voteRepository.existsByScriptAndApprover(script, approver)).thenReturn(false);
        when(voteRepository.findByScript(script)).thenReturn(List.of(v1, v2, v3));

        scriptService.vote(1L, approver, false);

        assertEquals(ScriptStatus.REJECTED, script.getStatus());
    }
}
