package me.agohar.usersec.security.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import me.agohar.usersec.security.model.UserDto;
import me.agohar.usersec.security.service.GcpIdentityService;



@RestController
@RequestMapping("/api/v1/user")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final GcpIdentityService gcpIdentityService;

    public UserController(GcpIdentityService gcpIdentityService) {
        this.gcpIdentityService = gcpIdentityService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        logger.info("User fetching profile: {}", email);
        UserDto user = gcpIdentityService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/groups")
    public ResponseEntity<List<String>> getUserGroups(Authentication authentication) {
        String email = authentication.getName();
        logger.info("User fetching groups: {}", email);
        List<String> groups = gcpIdentityService.getUserGroups(email);
        return ResponseEntity.ok(groups);
    }
}