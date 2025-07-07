package me.agohar.usersec.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.agohar.usersec.security.model.AddUserToGroupRequest;
import me.agohar.usersec.security.model.CreateUserRequest;
import me.agohar.usersec.security.model.GroupDto;
import me.agohar.usersec.security.model.UserDto;
import me.agohar.usersec.security.service.AuditService;
import me.agohar.usersec.security.service.GcpIdentityService;

@RestController
@RequestMapping("/api/v1/admin-audit")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Audit", description = "Administrative operations with audit logging for user and group management")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminControllerWithAudit {

    private static final Logger logger = LoggerFactory.getLogger(AdminControllerWithAudit.class);
    
    private final GcpIdentityService gcpIdentityService;
    private final AuditService auditService;

    public AdminControllerWithAudit(GcpIdentityService gcpIdentityService, AuditService auditService) {
        this.gcpIdentityService = gcpIdentityService;
        this.auditService = auditService;
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new user with audit", description = "Creates a new user in the Google Workspace directory with audit logging")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully with audit log",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "User creation request data", required = true)
            @Valid @RequestBody CreateUserRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        String adminEmail = authentication.getName();
        logger.info("Admin {} creating user: {}", adminEmail, request.email());
        
        UserDto user = gcpIdentityService.createUser(request);
        auditService.logUserCreation(adminEmail, request.email());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email,
                                         Authentication authentication) {
        String adminEmail = authentication.getName();
        logger.info("Admin {} deleting user: {}", adminEmail, email);
        
        gcpIdentityService.deleteUser(email);
        auditService.logUserDeletion(adminEmail, email);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{email}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable String email,
                                          Authentication authentication) {
        String adminEmail = authentication.getName();
        logger.info("Admin {} suspending user: {}", adminEmail, email);
        
        gcpIdentityService.suspendUser(email);
        auditService.logUserSuspension(adminEmail, email);
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/groups")
    public ResponseEntity<GroupDto> createGroup(@Valid @RequestBody GroupDto groupDto,
                                              Authentication authentication) {
        String adminEmail = authentication.getName();
        logger.info("Admin {} creating group: {}", adminEmail, groupDto.email());
        
        GroupDto group = gcpIdentityService.createGroup(groupDto);
        auditService.logGroupCreation(adminEmail, groupDto.email());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @PostMapping("/groups/members")
    public ResponseEntity<Void> addUserToGroup(@Valid @RequestBody AddUserToGroupRequest request,
                                             Authentication authentication) {
        String adminEmail = authentication.getName();
        logger.info("Admin {} adding user {} to group {}", adminEmail, request.userEmail(), request.groupEmail());
        
        gcpIdentityService.addUserToGroup(request.userEmail(), request.groupEmail());
        auditService.logUserAddedToGroup(adminEmail, request.userEmail(), request.groupEmail());
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/groups/{groupEmail}/members/{userEmail}")
    public ResponseEntity<Void> removeUserFromGroup(@PathVariable String groupEmail, 
                                                   @PathVariable String userEmail,
                                                   Authentication authentication) {
        String adminEmail = authentication.getName();
        logger.info("Admin {} removing user {} from group {}", adminEmail, userEmail, groupEmail);
        
        gcpIdentityService.removeUserFromGroup(userEmail, groupEmail);
        auditService.logUserRemovedFromGroup(adminEmail, userEmail, groupEmail);
        
        return ResponseEntity.ok().build();
    }

    // Other endpoints remain the same...
}
