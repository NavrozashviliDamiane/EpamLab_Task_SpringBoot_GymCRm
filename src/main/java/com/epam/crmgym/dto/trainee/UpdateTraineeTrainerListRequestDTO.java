package com.epam.crmgym.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTraineeTrainerListRequestDTO {

    @NotNull(message = "username is required")
    @NotBlank(message = "username is required")
    private String traineeUsername;

    @NotNull(message = "password is required")
    @NotBlank(message = "password is required")
    private String password;

    @NotEmpty(message = "trainer usernames list is required")
    @NotNull(message = "trainer usernames list is required")
    private List<String> trainerUsernames;

}

