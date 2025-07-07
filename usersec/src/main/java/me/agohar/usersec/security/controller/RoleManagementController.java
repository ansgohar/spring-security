package me.agohar.usersec.security.controller;


import jakarta.validation.Valid;
import me.agohar.usersec.security.model.RoleDto;
import me.agohar.usersec.security.service.RoleManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {

    private static final Logger logger = LoggerFactory.getLogger(RoleManagementController.class);
    
    private final RoleManagementService roleManagementService;

    public RoleManagementController(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        logger.info("Admin creating role: {}", roleDto.name());
        RoleDto role = roleManagementService.createRole(roleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        logger.info("Admin fetching all roles");
        List<RoleDto> roles = roleManagementService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{name}")
    public ResponseEntity<RoleDto> getRoleByName(@PathVariable String name) {
        logger.info("Admin fetching role: {}", name);
        RoleDto role = roleManagementService.getRoleByName(name);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{name}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable String name, 
                                            @Valid @RequestBody RoleDto roleDto) {
        logger.info("Admin updating role: {}", name);
        RoleDto role = roleManagementService.updateRole(name, roleDto);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteRole(@PathVariable String name) {
        logger.info("Admin deleting role: {}", name);
        roleManagementService.deleteRole(name);
        return ResponseEntity.noContent().build();
    }
}

