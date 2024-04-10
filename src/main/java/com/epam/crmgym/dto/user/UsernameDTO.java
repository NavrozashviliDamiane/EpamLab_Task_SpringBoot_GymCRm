package com.epam.crmgym.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsernameDTO {

    @NotNull(message = "Username is required")
    @NotBlank(message = "Username is required")
    private String username;

}

