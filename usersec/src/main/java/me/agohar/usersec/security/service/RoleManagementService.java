package me.agohar.usersec.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import me.agohar.usersec.security.model.RoleDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class RoleManagementService {

    private static final Logger logger = LoggerFactory.getLogger(RoleManagementService.class);
    
    // In-memory storage for roles - replace with database in production
    private final Map<String, RoleDto> roles = new HashMap<>();
    
    public RoleManagementService() {
        initializeDefaultRoles();
    }
    
    private void initializeDefaultRoles() {
        // Create default roles
        createRole(new RoleDto("ADMIN", "Administrator with full access", 
            List.of("CREATE_USER", "DELETE_USER", "MODIFY_USER", "CREATE_GROUP", "DELETE_GROUP", "MODIFY_GROUP"), 
            LocalDateTime.now(), LocalDateTime.now()));
            
        createRole(new RoleDto("USER", "Standard user with limited access", 
            List.of("READ_PROFILE", "UPDATE_PROFILE"), 
            LocalDateTime.now(), LocalDateTime.now()));
            
        createRole(new RoleDto("MANAGER", "Manager with user management capabilities", 
            List.of("CREATE_USER", "MODIFY_USER", "READ_USERS", "CREATE_GROUP", "MODIFY_GROUP"), 
            LocalDateTime.now(), LocalDateTime.now()));
    }
    
    public RoleDto createRole(RoleDto roleDto) {
        if (roles.containsKey(roleDto.name())) {
            throw new RuntimeException("Role already exists: " + roleDto.name());
        }
        
        RoleDto newRole = new RoleDto(
            roleDto.name(),
            roleDto.description(),
            new ArrayList<>(roleDto.permissions()),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        roles.put(roleDto.name(), newRole);
        logger.info("Created role: {}", roleDto.name());
        return newRole;
    }
    
    public List<RoleDto> getAllRoles() {
        return new ArrayList<>(roles.values());
    }
    
    public RoleDto getRoleByName(String name) {
        RoleDto role = roles.get(name);
        if (role == null) {
            throw new RuntimeException("Role not found: " + name);
        }
        return role;
    }
    
    public RoleDto updateRole(String name, RoleDto roleDto) {
        if (!roles.containsKey(name)) {
            throw new RuntimeException("Role not found: " + name);
        }
        
        RoleDto existingRole = roles.get(name);
        RoleDto updatedRole = new RoleDto(
            name,
            roleDto.description() != null ? roleDto.description() : existingRole.description(),
            roleDto.permissions() != null ? new ArrayList<>(roleDto.permissions()) : existingRole.permissions(),
            existingRole.createdAt(),
            LocalDateTime.now()
        );
        
        roles.put(name, updatedRole);
        logger.info("Updated role: {}", name);
        return updatedRole;
    }
    
    public void deleteRole(String name) {
        if (!roles.containsKey(name)) {
            throw new RuntimeException("Role not found: " + name);
        }
        
        // Prevent deletion of default roles
        if (List.of("ADMIN", "USER", "MANAGER").contains(name)) {
            throw new RuntimeException("Cannot delete default role: " + name);
        }
        
        roles.remove(name);
        logger.info("Deleted role: {}", name);
    }
    
    public boolean hasPermission(String roleName, String permission) {
        RoleDto role = roles.get(roleName);
        return role != null && role.permissions().contains(permission);
    }
    
    public List<String> getUserPermissions(List<String> userRoles) {
        return userRoles.stream()
            .map(roles::get)
            .filter(role -> role != null)
            .flatMap(role -> role.permissions().stream())
            .distinct()
            .collect(Collectors.toList());
    }
}

