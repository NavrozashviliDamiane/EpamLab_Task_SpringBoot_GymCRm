package com.epam.crmgym.util.trainer;

import com.epam.crmgym.entity.Training;
import com.epam.crmgym.repository.TrainingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TrainingQueryConstructor {

    private final TrainingRepository trainingRepository;

    public TrainingQueryConstructor(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }


    public List<Training> constructQuery(Long trainerId, Date fromDate, Date toDate, Long traineeId) {
        if (trainerId == null) {
            throw new IllegalArgumentException("Trainer ID cannot be null.");
        }

        if (fromDate != null && toDate != null && traineeId != null) {
            return trainingRepository.findByTrainerIdAndTrainingDateBetweenAndTraineeId(trainerId, fromDate, toDate, traineeId);
        } else if (fromDate != null && toDate != null) {
            return trainingRepository.findByTrainerIdAndTrainingDateBetween(trainerId, fromDate, toDate);
        } else if (fromDate != null) {
            return trainingRepository.findByTrainerIdAndTrainingDateAfter(trainerId, fromDate);
        } else if (toDate != null) {
            return trainingRepository.findByTrainerIdAndTrainingDateBefore(trainerId, toDate);
        } else if (traineeId != null) {
            return trainingRepository.findByTrainerIdAndTraineeId(trainerId, traineeId);
        } else {
            return trainingRepository.findByTrainerId(trainerId);
        }
    }
}
