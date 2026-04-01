package com.yourapp.security;

import com.yourapp.exceptions.ForbiddenException;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    public boolean canMutateTeamResources(String role) {
        return "admin".equalsIgnoreCase(role) || "owner".equalsIgnoreCase(role) || "member".equalsIgnoreCase(role);
    }

    public void assertCanMutateTeamResources(String role) {
        if (!canMutateTeamResources(role)) {
            throw new ForbiddenException("You do not have permission to mutate this resource.");
        }
    }
}
