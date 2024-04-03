package com.epam.crmgym.dto.trainer;

import com.epam.crmgym.validation.ValidTrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainerRegistrationRequest {

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Specialization name is required")
    @NotBlank(message = "Specialization name is required")
    @ValidTrainingType
    private String specialization;
}
