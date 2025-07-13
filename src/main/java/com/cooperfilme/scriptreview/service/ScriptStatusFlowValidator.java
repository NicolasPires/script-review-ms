package com.cooperfilme.scriptreview.service;

import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.exception.InvalidStatusTransitionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScriptStatusFlowValidator {

    private final RoleBasedStatusTransition roleBasedStatusTransition;

    public void validate(ScriptStatus current, ScriptStatus target) {
        String role = getLoggedUserRole();

        boolean allowed = roleBasedStatusTransition.isAllowed(role, current, target);

        if (!allowed) {
            throw new InvalidStatusTransitionException(
                    String.format("User with role '%s' is not allowed to transition from %s to %s", role, current, target)
            );
        }
    }

    private String getLoggedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities() == null) {
            throw new AccessDeniedException("User is not authenticated or has no assigned role");
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("User has no assigned role"));
    }
}
