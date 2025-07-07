package me.agohar.usersec.security.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record RoleDto(
        @NotBlank(message = "Role name is required") String name,

        String description,
        List<String> permissions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
