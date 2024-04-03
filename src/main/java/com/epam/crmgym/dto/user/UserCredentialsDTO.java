package com.epam.crmgym.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCredentialsDTO {

    @NotNull(message = "Username is required")
    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "password is required")
    @NotBlank(message = "Password is required")
    private String password;
}
