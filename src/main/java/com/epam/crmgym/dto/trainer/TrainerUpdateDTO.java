package com.epam.crmgym.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerUpdateDTO {


    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String username;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String password;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String lastName;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private boolean isActive;

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String specialization;
}

