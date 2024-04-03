package com.epam.crmgym.service;

import com.epam.crmgym.entity.Training;
import com.epam.crmgym.entity.TrainingTypeValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public interface TrainingService {

    List<Training> getAllTrainings(String username, String password);

    Training getTrainingById(Long id, String username, String password);



    @Transactional
    Training createTraining(String traineeUsername, String trainerUsername,
                            String trainingName, Date trainingDate, Integer trainingDuration,
                            String password);

    void deleteTraining(Long id, String username, String password);

    void updateTrainingForTrainee(String username);

    List<Training> getTrainingsByTraineeUsernameAndCriteria(String username, String password, Date fromDate, Date toDate, String trainerName, TrainingTypeValue trainingTypeName);

    List<Training> getTrainingsByTrainerUsernameAndCriteria(String username, String password, Date fromDate, Date toDate, String traineeName);
}
