package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.enums.ScriptStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleBasedStatusTransition {

    private static final Map<String, Map<ScriptStatus, Set<ScriptStatus>>> transitions = new HashMap<>();

    static {
        Map<ScriptStatus, Set<ScriptStatus>> allRoles = new HashMap<>();
        allRoles.put(ScriptStatus.AWAITING_ANALYSIS, Set.of(ScriptStatus.IN_ANALYSIS));
        transitions.put("ANY", allRoles);

        Map<ScriptStatus, Set<ScriptStatus>> analyst = new HashMap<>();
        analyst.put(ScriptStatus.IN_ANALYSIS, Set.of(ScriptStatus.AWAITING_REVIEW, ScriptStatus.REJECTED));
        transitions.put("ANALYST", analyst);

        Map<ScriptStatus, Set<ScriptStatus>> reviewer = new HashMap<>();
        reviewer.put(ScriptStatus.AWAITING_REVIEW, Set.of(ScriptStatus.IN_REVIEW));
        reviewer.put(ScriptStatus.IN_REVIEW, Set.of(ScriptStatus.AWAITING_APPROVAL));
        transitions.put("REVIEWER", reviewer);

        Map<ScriptStatus, Set<ScriptStatus>> approver = new HashMap<>();
        approver.put(ScriptStatus.AWAITING_APPROVAL, Set.of(ScriptStatus.IN_APPROVAL, ScriptStatus.REJECTED));
        approver.put(ScriptStatus.IN_APPROVAL, Set.of(ScriptStatus.APPROVED, ScriptStatus.REJECTED));
        transitions.put("APPROVER", approver);
    }

    public boolean isAllowed(String role, ScriptStatus current, ScriptStatus target) {
        if (transitions.get("ANY").getOrDefault(current, Collections.emptySet()).contains(target)) {
            return true;
        }

        return transitions.getOrDefault(role, Collections.emptyMap())
                .getOrDefault(current, Collections.emptySet())
                .contains(target);
    }
}
