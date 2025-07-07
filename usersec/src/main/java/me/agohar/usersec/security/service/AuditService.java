package me.agohar.usersec.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    public void logUserCreation(String adminEmail, String userEmail) {
        Map<String, Object> auditEvent = createAuditEvent(
            "USER_CREATED",
            adminEmail,
            "User created: " + userEmail
        );
        logAuditEvent(auditEvent);
    }

    public void logUserDeletion(String adminEmail, String userEmail) {
        Map<String, Object> auditEvent = createAuditEvent(
            "USER_DELETED",
            adminEmail,
            "User deleted: " + userEmail
        );
        logAuditEvent(auditEvent);
    }

    public void logUserSuspension(String adminEmail, String userEmail) {
        Map<String, Object> auditEvent = createAuditEvent(
            "USER_SUSPENDED",
            adminEmail,
            "User suspended: " + userEmail
        );
        logAuditEvent(auditEvent);
    }

    public void logGroupCreation(String adminEmail, String groupEmail) {
        Map<String, Object> auditEvent = createAuditEvent(
            "GROUP_CREATED",
            adminEmail,
            "Group created: " + groupEmail
        );
        logAuditEvent(auditEvent);
    }

    public void logUserAddedToGroup(String adminEmail, String userEmail, String groupEmail) {
        Map<String, Object> auditEvent = createAuditEvent(
            "USER_ADDED_TO_GROUP",
            adminEmail,
            String.format("User %s added to group %s", userEmail, groupEmail)
        );
        logAuditEvent(auditEvent);
    }

    public void logUserRemovedFromGroup(String adminEmail, String userEmail, String groupEmail) {
        Map<String, Object> auditEvent = createAuditEvent(
            "USER_REMOVED_FROM_GROUP",
            adminEmail,
            String.format("User %s removed from group %s", userEmail, groupEmail)
        );
        logAuditEvent(auditEvent);
    }

    public void logRoleCreation(String adminEmail, String roleName) {
        Map<String, Object> auditEvent = createAuditEvent(
            "ROLE_CREATED",
            adminEmail,
            "Role created: " + roleName
        );
        logAuditEvent(auditEvent);
    }

    public void logRoleUpdate(String adminEmail, String roleName) {
        Map<String, Object> auditEvent = createAuditEvent(
            "ROLE_UPDATED",
            adminEmail,
            "Role updated: " + roleName
        );
        logAuditEvent(auditEvent);
    }

    public void logRoleDeletion(String adminEmail, String roleName) {
        Map<String, Object> auditEvent = createAuditEvent(
            "ROLE_DELETED",
            adminEmail,
            "Role deleted: " + roleName
        );
        logAuditEvent(auditEvent);
    }

    public void logUnauthorizedAccess(String userEmail, String resource) {
        Map<String, Object> auditEvent = createAuditEvent(
            "UNAUTHORIZED_ACCESS",
            userEmail,
            "Unauthorized access attempt to: " + resource
        );
        logAuditEvent(auditEvent);
    }

    private Map<String, Object> createAuditEvent(String eventType, String userEmail, String description) {
        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", LocalDateTime.now());
        event.put("eventType", eventType);
        event.put("userEmail", userEmail);
        event.put("description", description);
        return event;
    }

    private void logAuditEvent(Map<String, Object> event) {
        auditLogger.info("AUDIT_EVENT: {}", event);
    }
}

