package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScriptStatusFlowValidatorTest {

    private final ScriptStatusFlowValidator validator = new ScriptStatusFlowValidator();

    @Test
    void shouldAllowValidTransition() {
        assertDoesNotThrow(() ->
                validator.validate(ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.IN_ANALYSIS));
    }

    @Test
    void shouldThrowOnInvalidTransition() {
        assertThrows(InvalidStatusTransitionException.class, () ->
                validator.validate(ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.APPROVED));
    }
}

