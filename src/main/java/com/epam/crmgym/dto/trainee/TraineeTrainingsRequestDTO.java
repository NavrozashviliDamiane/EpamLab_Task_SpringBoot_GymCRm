package com.epam.crmgym.dto.trainee;

import com.epam.crmgym.exception.DateDeSerializer;
import com.epam.crmgym.validation.DateRange;
import com.epam.crmgym.validation.ValidTrainingType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
@DateRange(message = "From date must be before or equal to the to date.")
public class TraineeTrainingsRequestDTO {

    @NotNull(message = "username is required")
    @NotBlank(message = "username is required")
    private String username;

    @NotNull(message = "password is required")
    @NotBlank(message = "password is required")
    private String password;

    @JsonDeserialize(using = DateDeSerializer.class)
    private Date fromDate;

    @JsonDeserialize(using = DateDeSerializer.class)
    private Date toDate;

    private String trainerName;

    @ValidTrainingType
    private String trainingTypeName;

}
