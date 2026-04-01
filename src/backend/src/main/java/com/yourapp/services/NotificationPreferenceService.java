package com.yourapp.services;

import com.yourapp.dtos.UpdateNotificationPreferenceRequest;
import com.yourapp.entities.NotificationPreference;
import com.yourapp.entities.Organisation;
import com.yourapp.entities.User;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.NotificationPreferenceRepository;
import com.yourapp.repositories.OrganisationRepository;
import com.yourapp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationPreferenceService {
    private final NotificationPreferenceRepository repo;
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;

    public NotificationPreferenceService(
            NotificationPreferenceRepository repo,
            UserRepository userRepository,
            OrganisationRepository organisationRepository
    ) {
        this.repo = repo;
        this.userRepository = userRepository;
        this.organisationRepository = organisationRepository;
    }

    private User demoUser() {
        return userRepository.findByEmail("demo@acme.dev")
                .orElseThrow(() -> new NotFoundException("Demo user not found"));
    }

    private Organisation demoOrg() {
        return organisationRepository.findBySlug("acme")
                .orElseThrow(() -> new NotFoundException("Demo org not found"));
    }

    public List<NotificationPreference> list() {
        return repo.findByUserIdOrderByEventTypeAscChannelAsc(demoUser().getId());
    }

    public NotificationPreference upsert(UpdateNotificationPreferenceRequest request) {
        User user = demoUser();
        Organisation org = demoOrg();
        NotificationPreference p = repo
                .findByUserIdAndEventTypeAndChannel(user.getId(), request.eventType(), request.channel())
                .orElseGet(() -> {
                    NotificationPreference np = new NotificationPreference();
                    np.setUser(user);
                    np.setOrganisation(org);
                    np.setEventType(request.eventType());
                    np.setChannel(request.channel());
                    return np;
                });
        p.setEnabled(request.enabled());
        return repo.save(p);
    }
}
