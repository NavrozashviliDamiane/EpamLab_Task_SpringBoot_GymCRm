package com.epam.crmgym.dto.trainee;

import com.epam.crmgym.dto.trainer.TrainerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTraineeTrainersRequestDTO {
    private String traineeUsername;
    private List<TrainerDTO> trainersList;
}
