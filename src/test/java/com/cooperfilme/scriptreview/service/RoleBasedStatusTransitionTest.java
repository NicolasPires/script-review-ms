package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.enums.ScriptStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleBasedStatusTransitionTest {

    private RoleBasedStatusTransition transition;

    @BeforeEach
    void setUp() {
        transition = new RoleBasedStatusTransition();
    }

    @Test
    void shouldAllowAnyRoleTransitionFromAwaitingAnalysisToInAnalysis() {
        assertTrue(transition.isAllowed("ANY", ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.IN_ANALYSIS));
        assertTrue(transition.isAllowed("ANALYST", ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.IN_ANALYSIS));
        assertTrue(transition.isAllowed("REVIEWER", ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.IN_ANALYSIS));
        assertTrue(transition.isAllowed("APPROVER", ScriptStatus.AWAITING_ANALYSIS, ScriptStatus.IN_ANALYSIS));
    }

    @Test
    void shouldAllowAnalystTransitionFromInAnalysisToAwaitingReviewOrRejected() {
        assertTrue(transition.isAllowed("ANALYST", ScriptStatus.IN_ANALYSIS, ScriptStatus.AWAITING_REVIEW));
        assertTrue(transition.isAllowed("ANALYST", ScriptStatus.IN_ANALYSIS, ScriptStatus.REJECTED));
    }

    @Test
    void shouldAllowReviewerTransitions() {
        assertTrue(transition.isAllowed("REVIEWER", ScriptStatus.AWAITING_REVIEW, ScriptStatus.IN_REVIEW));
        assertTrue(transition.isAllowed("REVIEWER", ScriptStatus.IN_REVIEW, ScriptStatus.AWAITING_APPROVAL));
    }

    @Test
    void shouldAllowApproverTransitions() {
        assertTrue(transition.isAllowed("APPROVER", ScriptStatus.AWAITING_APPROVAL, ScriptStatus.IN_APPROVAL));
        assertTrue(transition.isAllowed("APPROVER", ScriptStatus.AWAITING_APPROVAL, ScriptStatus.REJECTED));
        assertTrue(transition.isAllowed("APPROVER", ScriptStatus.IN_APPROVAL, ScriptStatus.APPROVED));
        assertTrue(transition.isAllowed("APPROVER", ScriptStatus.IN_APPROVAL, ScriptStatus.REJECTED));
    }

    @Test
    void shouldNotAllowInvalidTransitions() {
        assertFalse(transition.isAllowed("ANALYST", ScriptStatus.IN_REVIEW, ScriptStatus.APPROVED));
        assertFalse(transition.isAllowed("REVIEWER", ScriptStatus.IN_ANALYSIS, ScriptStatus.AWAITING_REVIEW));
        assertFalse(transition.isAllowed("APPROVER", ScriptStatus.IN_REVIEW, ScriptStatus.APPROVED));
        assertFalse(transition.isAllowed("UNKNOWN_ROLE", ScriptStatus.IN_ANALYSIS, ScriptStatus.AWAITING_REVIEW));
    }

    @Test
    void shouldReturnFalseWhenNoMappingExists() {
        assertFalse(transition.isAllowed("ANALYST", ScriptStatus.APPROVED, ScriptStatus.REJECTED));
        assertFalse(transition.isAllowed("REVIEWER", ScriptStatus.REJECTED, ScriptStatus.APPROVED));
    }
}
