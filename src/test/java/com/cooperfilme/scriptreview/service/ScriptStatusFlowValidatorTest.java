package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScriptStatusFlowValidatorTest {

    private RoleBasedStatusTransition roleBasedStatusTransition;
    private ScriptStatusFlowValidator validator;

    @BeforeEach
    void setUp() {
        roleBasedStatusTransition = Mockito.mock(RoleBasedStatusTransition.class);
        validator = new ScriptStatusFlowValidator(roleBasedStatusTransition);

        var authority = new SimpleGrantedAuthority("ANALYST");
        var auth = new UsernamePasswordAuthenticationToken("user", "password", Collections.singletonList(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowValidTransition() {
        Mockito.when(roleBasedStatusTransition.isAllowed("ANALYST", ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.IN_ANALYSIS))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                validator.validate(ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.IN_ANALYSIS));
    }

    @Test
    void shouldThrowOnInvalidTransition() {
        Mockito.when(roleBasedStatusTransition.isAllowed("ANALYST", ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.APPROVED))
                .thenReturn(false);

        assertThrows(InvalidStatusTransitionException.class, () ->
                validator.validate(ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.APPROVED));
    }
}
