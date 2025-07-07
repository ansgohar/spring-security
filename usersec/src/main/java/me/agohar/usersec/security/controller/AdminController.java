package me.agohar.usersec.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.agohar.usersec.security.model.AddUserToGroupRequest;
import me.agohar.usersec.security.model.CreateUserRequest;
import me.agohar.usersec.security.model.GroupDto;
import me.agohar.usersec.security.model.UserDto;
import me.agohar.usersec.security.service.GcpIdentityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrative operations for user and group management")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final GcpIdentityService gcpIdentityService;

    public AdminController(GcpIdentityService gcpIdentityService) {
        this.gcpIdentityService = gcpIdentityService;
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new user", description = "Creates a new user in the Google Workspace directory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "User creation request data", required = true)
            @Valid @RequestBody CreateUserRequest request) {
        logger.info("Admin creating user: {}", request.email());
        UserDto user = gcpIdentityService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users from the Google Workspace directory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.info("Admin fetching all users");
        List<UserDto> users = gcpIdentityService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves a specific user by their email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDto> getUserByEmail(
            @Parameter(description = "User email address", required = true, example = "user@example.com")
            @PathVariable String email) {
        logger.info("Admin fetching user: {}", email);
        UserDto user = gcpIdentityService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{email}")
    @Operation(summary = "Delete user", description = "Permanently deletes a user from the Google Workspace directory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User email address", required = true, example = "user@example.com")
            @PathVariable String email) {
        logger.info("Admin deleting user: {}", email);
        gcpIdentityService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{email}/suspend")
    @Operation(summary = "Suspend user", description = "Suspends a user account in the Google Workspace directory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User suspended successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> suspendUser(
            @Parameter(description = "User email address", required = true, example = "user@example.com")
            @PathVariable String email) {
        logger.info("Admin suspending user: {}", email);
        gcpIdentityService.suspendUser(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/groups")
    @Operation(summary = "Create a new group", description = "Creates a new group in the Google Workspace directory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Group created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "409", description = "Group already exists")
    })
    public ResponseEntity<GroupDto> createGroup(
            @Parameter(description = "Group creation data", required = true)
            @Valid @RequestBody GroupDto groupDto) {
        logger.info("Admin creating group: {}", groupDto.email());
        GroupDto group = gcpIdentityService.createGroup(groupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @GetMapping("/groups")
    @Operation(summary = "Get all groups", description = "Retrieves a list of all groups from the Google Workspace directory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Groups retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        logger.info("Admin fetching all groups");
        List<GroupDto> groups = gcpIdentityService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/groups/members")
    @Operation(summary = "Add user to group", description = "Adds a user to a specific group")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User added to group successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User or group not found")
    })
    public ResponseEntity<Void> addUserToGroup(
            @Parameter(description = "Request to add user to group", required = true)
            @Valid @RequestBody AddUserToGroupRequest request) {
        logger.info("Admin adding user {} to group {}", request.userEmail(), request.groupEmail());
        gcpIdentityService.addUserToGroup(request.userEmail(), request.groupEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/groups/{groupEmail}/members/{userEmail}")
    @Operation(summary = "Remove user from group", description = "Removes a user from a specific group")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User removed from group successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User or group not found")
    })
    public ResponseEntity<Void> removeUserFromGroup(
            @Parameter(description = "Group email address", required = true, example = "group@example.com")
            @PathVariable String groupEmail,
            @Parameter(description = "User email address", required = true, example = "user@example.com")
            @PathVariable String userEmail) {
        logger.info("Admin removing user {} from group {}", userEmail, groupEmail);
        gcpIdentityService.removeUserFromGroup(userEmail, groupEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{email}/groups")
    @Operation(summary = "Get user groups", description = "Retrieves all groups that a user belongs to")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User groups retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<String>> getUserGroups(
            @Parameter(description = "User email address", required = true, example = "user@example.com")
            @PathVariable String email) {
        logger.info("Admin fetching groups for user: {}", email);
        List<String> groups = gcpIdentityService.getUserGroups(email);
        return ResponseEntity.ok(groups);
    }
}


