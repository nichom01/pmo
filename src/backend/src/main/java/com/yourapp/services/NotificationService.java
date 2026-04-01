package com.yourapp.services;

import com.yourapp.entities.Issue;
import com.yourapp.entities.Notification;
import com.yourapp.entities.User;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.NotificationRepository;
import com.yourapp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public User demoUser() {
        return userRepository.findByEmail("demo@acme.dev")
                .orElseThrow(() -> new NotFoundException("Demo user not found"));
    }

    public void create(User recipient, User actor, Issue issue, String type) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setActor(actor);
        n.setIssue(issue);
        n.setType(type);
        notificationRepository.save(n);
    }

    public List<Notification> listForDemoUser() {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(demoUser().getId());
    }

    public Notification markRead(UUID id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found: " + id));
        n.setReadAt(OffsetDateTime.now());
        return notificationRepository.save(n);
    }

    public int markAllReadForDemoUser() {
        List<Notification> notifications = listForDemoUser();
        notifications.forEach(n -> n.setReadAt(OffsetDateTime.now()));
        notificationRepository.saveAll(notifications);
        return notifications.size();
    }
}
