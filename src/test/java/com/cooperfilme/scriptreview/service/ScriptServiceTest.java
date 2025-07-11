package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.entity.Script;
import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.exception.NotFoundException;
import com.cooperfilme.scriptreview.repository.ScriptRepository;
import com.cooperfilme.scriptreview.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptServiceTest {

    @Mock
    ScriptRepository scriptRepository;
    @Mock
    VoteRepository voteRepository;
    @Mock ScriptStatusFlowValidator validator;

    @InjectMocks
    ScriptService scriptService;

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
    void shouldThrowWhenScriptNotFound() {
        when(scriptRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                scriptService.updateStatus(99L, ScriptStatus.APPROVED));
    }
}

