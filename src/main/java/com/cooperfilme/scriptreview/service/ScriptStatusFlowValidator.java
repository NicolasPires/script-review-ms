package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.exception.InvalidStatusTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ScriptStatusFlowValidator {

    private static final Map<ScriptStatus, List<ScriptStatus>> TRANSITIONS = new EnumMap<>(ScriptStatus.class);

    static {
        TRANSITIONS.put(ScriptStatus.AWAITING_ANALYSIS, List.of(ScriptStatus.IN_ANALYSIS));
        TRANSITIONS.put(ScriptStatus.IN_ANALYSIS, List.of(ScriptStatus.AWAITING_REVIEW, ScriptStatus.REJECTED));
        TRANSITIONS.put(ScriptStatus.AWAITING_REVIEW, List.of(ScriptStatus.IN_REVIEW));
        TRANSITIONS.put(ScriptStatus.IN_REVIEW, List.of(ScriptStatus.AWAITING_APPROVAL));
        TRANSITIONS.put(ScriptStatus.AWAITING_APPROVAL, List.of(ScriptStatus.IN_APPROVAL, ScriptStatus.REJECTED));
        TRANSITIONS.put(ScriptStatus.IN_APPROVAL, List.of(ScriptStatus.APPROVED, ScriptStatus.REJECTED));
    }

    public void validate(ScriptStatus current, ScriptStatus target) {
        if (!TRANSITIONS.getOrDefault(current, List.of()).contains(target)) {
            throw new InvalidStatusTransitionException("Invalid transition: " + current + " → " + target);
        }
    }
}
