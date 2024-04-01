package com.epam.crmgym.dto.user;

import com.epam.crmgym.validation.RequiredBoolean;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
public class UpdateUserStatusRequestDTO {

    @NotNull(message = "Username is required")
    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Status field is required")
    @RequiredBoolean(message = "Status field is required")
    private Boolean isActive;
}
