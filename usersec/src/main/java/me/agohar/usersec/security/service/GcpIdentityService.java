package me.agohar.usersec.security.service;

import com.google.api.services.directory.Directory;
import com.google.api.services.directory.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.agohar.usersec.security.config.GcpConfig;
import me.agohar.usersec.security.model.CreateUserRequest;
import me.agohar.usersec.security.model.GroupDto;
import me.agohar.usersec.security.model.UserDto;

@Service
public class GcpIdentityService { private static final Logger logger = LoggerFactory.getLogger(GcpIdentityService.class);
    
    private final Directory directoryService;
    private final GcpConfig gcpConfig;

    public GcpIdentityService(Directory directoryService, 
                             GcpConfig gcpConfig) {
        this.directoryService = directoryService;
        this.gcpConfig = gcpConfig;
    }

    public UserDto createUser(CreateUserRequest request) {
        try {
            User user = new User()
                .setName(new UserName()
                    .setGivenName(request.firstName())
                    .setFamilyName(request.lastName()))
                .setPrimaryEmail(request.email())
                .setPassword(request.password())
                .setChangePasswordAtNextLogin(true);

            User createdUser = directoryService.users().insert(user).execute();
            logger.info("Created user: {}", createdUser.getPrimaryEmail());

            // Add user to groups if specified
            if (request.groups() != null && !request.groups().isEmpty()) {
                for (String groupEmail : request.groups()) {
                    addUserToGroup(request.email(), groupEmail);
                }
            }

            return mapToUserDto(createdUser);
        } catch (IOException e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    public List<UserDto> getAllUsers() {
        try {
            Users users = directoryService.users().list()
                .setDomain(gcpConfig.getDomain())
                .setMaxResults(500)
                .execute();

            return users.getUsers().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error fetching users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    public UserDto getUserByEmail(String email) {
        try {
            User user = directoryService.users().get(email).execute();
            return mapToUserDto(user);
        } catch (IOException e) {
            logger.error("Error fetching user {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    public void deleteUser(String email) {
        try {
            directoryService.users().delete(email).execute();
            logger.info("Deleted user: {}", email);
        } catch (IOException e) {
            logger.error("Error deleting user {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public void suspendUser(String email) {
        try {
            User user = new User().setSuspended(true);
            directoryService.users().update(email, user).execute();
            logger.info("Suspended user: {}", email);
        } catch (IOException e) {
            logger.error("Error suspending user {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to suspend user", e);
        }
    }

    public GroupDto createGroup(GroupDto groupDto) {
        try {
            Group group = new Group()
                .setName(groupDto.name())
                .setEmail(groupDto.email())
                .setDescription(groupDto.description());

            Group createdGroup = directoryService.groups().insert(group).execute();
            logger.info("Created group: {}", createdGroup.getEmail());

            return mapToGroupDto(createdGroup);
        } catch (IOException e) {
            logger.error("Error creating group: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create group", e);
        }
    }

    public List<GroupDto> getAllGroups() {
        try {
            Groups groups = directoryService.groups().list()
                .setDomain(gcpConfig.getDomain())
                .setMaxResults(200)
                .execute();

            return groups.getGroups().stream()
                .map(this::mapToGroupDto)
                .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error fetching groups: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch groups", e);
        }
    }

    public void addUserToGroup(String userEmail, String groupEmail) {
        try {
            Member member = new Member()
                .setEmail(userEmail)
                .setRole("MEMBER");

            directoryService.members().insert(groupEmail, member).execute();
            logger.info("Added user {} to group {}", userEmail, groupEmail);
        } catch (IOException e) {
            logger.error("Error adding user {} to group {}: {}", userEmail, groupEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to add user to group", e);
        }
    }

    public void removeUserFromGroup(String userEmail, String groupEmail) {
        try {
            directoryService.members().delete(groupEmail, userEmail).execute();
            logger.info("Removed user {} from group {}", userEmail, groupEmail);
        } catch (IOException e) {
            logger.error("Error removing user {} from group {}: {}", userEmail, groupEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to remove user from group", e);
        }
    }

    public List<String> getUserGroups(String userEmail) {
        try {
            Groups groups = directoryService.groups().list()
                .setUserKey(userEmail)
                .execute();

            return groups.getGroups().stream()
                .map(Group::getEmail)
                .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error fetching user groups for {}: {}", userEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch user groups", e);
        }
    }

    private UserDto mapToUserDto(User user) {
        List<String> groups = new ArrayList<>();
        try {
            groups = getUserGroups(user.getPrimaryEmail());
        } catch (Exception e) {
            logger.warn("Could not fetch groups for user {}: {}", user.getPrimaryEmail(), e.getMessage());
        }

        return new UserDto(
            user.getPrimaryEmail(),
            user.getName().getGivenName(),
            user.getName().getFamilyName(),
            groups,
            List.of(), // roles would be managed separately
            LocalDateTime.now(), // creation time not directly available
            LocalDateTime.now(),
            user.getSuspended() != null && user.getSuspended()
        );
    }

    private GroupDto mapToGroupDto(Group group) {
        List<String> members = new ArrayList<>();
        try {
            Members groupMembers = directoryService.members().list(group.getEmail()).execute();
            if (groupMembers.getMembers() != null) {
                members = groupMembers.getMembers().stream()
                    .map(Member::getEmail)
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            logger.warn("Could not fetch members for group {}: {}", group.getEmail(), e.getMessage());
        }

        return new GroupDto(
            group.getName(),
            group.getEmail(),
            group.getDescription(),
            members,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}