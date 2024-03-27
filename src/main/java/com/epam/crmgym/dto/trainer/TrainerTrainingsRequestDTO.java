package com.epam.crmgym.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerTrainingsRequestDTO {
    private String username;
    private Date periodFrom;
    private Date periodTo;
    private String traineeName;
}