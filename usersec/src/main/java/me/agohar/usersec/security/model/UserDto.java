package me.agohar.usersec.security.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @NotBlank(message = "First name is required") @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters") String firstName,

        @NotBlank(message = "Last name is required") @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters") String lastName,

        List<String> groups,
        List<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean suspended) {
}