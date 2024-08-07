package com.yechat.users.user.request;

import jakarta.validation.constraints.*;

public record UserRequest(
        @NotBlank(message = "Username is required")
        @NotNull(message = "Username is can not be null")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must be alphanumeric")
        @Size(min = 4, max = 16, message = "Username must be between 4 and 20 characters")
        String username,

        @Size(min = 2, max = 32, message = "Firstname must be between 2 and 32 characters")
        @NotNull(message = "Firstname is can not be null")
        @NotBlank(message = "Firstname is required")
        String firstname,

        @Size(min = 2, max = 32, message = "Lastname must be between 2 and 32 characters")
        @NotNull(message = "Lastname is can not be null")
        @NotBlank(message = "Lastname is required")
        String lastname,

        @NotBlank(message = "Email is required")
        @NotNull(message = "Email is can not be null")
        @Email(message = "Email is invalid")
        String email
) {
}
