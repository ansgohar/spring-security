package me.agohar.usersec.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request object for creating a new user")
public record CreateUserRequest(
        @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
        @NotBlank(message = "Email is required") 
        @Email(message = "Invalid email format") 
        String email,

        @Schema(description = "User's first name", example = "John", required = true)
        @NotBlank(message = "First name is required") 
        String firstName,

        @Schema(description = "User's last name", example = "Doe", required = true)
        @NotBlank(message = "Last name is required") 
        String lastName,

        @Schema(description = "User's initial password", example = "SecurePassword123!", required = true, minLength = 8)
        @NotBlank(message = "Password is required") 
        @Size(min = 8, message = "Password must be at least 8 characters") 
        String password,

        @Schema(description = "List of group emails to add the user to", example = "[\"engineers@example.com\", \"developers@example.com\"]")
        List<String> groups) {
}