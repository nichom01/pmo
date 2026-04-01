package com.yourapp.services;

import com.yourapp.security.PermissionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionServiceTest {

    private final PermissionService service = new PermissionService();

    @Test
    void canMutateForAdminOwnerMember() {
        assertTrue(service.canMutateTeamResources("admin"));
        assertTrue(service.canMutateTeamResources("owner"));
        assertTrue(service.canMutateTeamResources("member"));
    }

    @Test
    void cannotMutateForGuest() {
        assertFalse(service.canMutateTeamResources("guest"));
    }
}
