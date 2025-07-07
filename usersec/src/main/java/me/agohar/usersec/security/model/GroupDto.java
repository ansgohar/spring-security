package me.agohar.usersec.security.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupDto(
                @NotBlank(message = "Group name is required") @Size(min = 1, max = 100, message = "Group name must be between 1 and 100 characters") String name,

                @NotBlank(message = "Group email is required") @Email(message = "Invalid group email format") String email,

                String description,
                List<String> members,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
