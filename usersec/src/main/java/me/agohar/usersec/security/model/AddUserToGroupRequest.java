package me.agohar.usersec.security.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddUserToGroupRequest(
        @NotBlank(message = "User email is required") @Email(message = "Invalid user email format") String userEmail,

        @NotBlank(message = "Group email is required") @Email(message = "Invalid group email format") String groupEmail) {
}
