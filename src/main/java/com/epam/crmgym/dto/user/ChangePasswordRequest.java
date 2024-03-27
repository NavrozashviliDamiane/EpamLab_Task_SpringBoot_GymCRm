package com.epam.crmgym.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String username;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String oldPassword;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String newPassword;

}

