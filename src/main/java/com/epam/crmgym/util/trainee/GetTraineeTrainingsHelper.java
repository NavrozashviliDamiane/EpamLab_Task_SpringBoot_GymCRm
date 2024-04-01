package com.epam.crmgym.util.trainee;

import com.epam.crmgym.dto.training.TrainingDTO;
import com.epam.crmgym.entity.Trainer;
import com.epam.crmgym.entity.Training;
import com.epam.crmgym.entity.TrainingType;
import com.epam.crmgym.entity.TrainingTypeValue;
import com.epam.crmgym.repository.TrainerRepository;
import com.epam.crmgym.repository.TrainingRepository;
import com.epam.crmgym.repository.TrainingTypeRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetTraineeTrainingsHelper {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;

    public GetTraineeTrainingsHelper(TrainerRepository trainerRepository, TrainingTypeRepository trainingTypeRepository, TrainingRepository trainingRepository) {
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingRepository = trainingRepository;
    }

    public Long getTrainerId(String trainerName) {
        if (trainerName != null) {
            Trainer trainer = trainerRepository.findByUserUsername(trainerName);
            if (trainer != null) {
                return trainer.getId();
            }
        }
        return null;
    }

    public Long getTrainingTypeId(String trainingTypeName) {
        if (trainingTypeName != null) {
            TrainingType trainingType = trainingTypeRepository.findByTrainingType(TrainingTypeValue.valueOf(trainingTypeName));
            if (trainingType != null) {
                return trainingType.getId();
            }
        }
        return null;
    }

    public List<Training> constructQuery(Long traineeId, Date fromDate, Date toDate, Long trainerId, Long trainingTypeId) {
        // Check if traineeId is provided
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null.");
        }

        // Initialize the list of trainings
        List<Training> trainings;

        // Construct query based on provided parameters
        if (fromDate != null && toDate != null) {
            if (trainerId != null && trainingTypeId != null) {
                trainings = trainingRepository.findByTraineeIdAndTrainingDateBetweenAndTrainerIdAndTrainingTypeId(
                        traineeId, fromDate, toDate, trainerId, trainingTypeId);
            } else if (trainerId != null) {
                trainings = trainingRepository.findByTraineeIdAndTrainingDateBetweenAndTrainerId(
                        traineeId, fromDate, toDate, trainerId);
            } else if (trainingTypeId != null) {
                trainings = trainingRepository.findByTraineeIdAndTrainingDateBetweenAndTrainingTypeId(
                        traineeId, fromDate, toDate, trainingTypeId);
            } else {
                trainings = trainingRepository.findByTraineeIdAndTrainingDateBetween(
                        traineeId, fromDate, toDate);
            }
        } else {
            // Check if toDate is provided
            if (toDate != null) {
                if (trainerId != null && trainingTypeId != null) {
                    trainings = trainingRepository.findByTraineeIdAndTrainingDateBeforeAndTrainerIdAndTrainingTypeId(
                            traineeId, toDate, trainerId, trainingTypeId);
                } else if (trainerId != null) {
                    trainings = trainingRepository.findByTraineeIdAndTrainingDateBeforeAndTrainerId(
                            traineeId, toDate, trainerId);
                } else if (trainingTypeId != null) {
                    trainings = trainingRepository.findByTraineeIdAndTrainingDateBeforeAndTrainingTypeId(
                            traineeId, toDate, trainingTypeId);
                } else {
                    trainings = trainingRepository.findByTraineeIdAndTrainingDateBefore(
                            traineeId, toDate);
                }
            } else {
                // If neither fromDate nor toDate is provided, return all trainings for the trainee
                trainings = trainingRepository.findByTraineeId(traineeId);
            }
        }

        return trainings;
    }




    public List<TrainingDTO> mapToTrainingDTO(List<Training> trainings) {
        return trainings.stream()
                .map(training -> {
                    TrainingDTO trainingDTO = new TrainingDTO();
                    trainingDTO.setTrainingName(training.getTrainingName());
                    trainingDTO.setTrainingDate(training.getTrainingDate());
                    if (training.getTrainingType() != null) {
                        trainingDTO.setTrainingType(training.getTrainingType().getTrainingType().toString());
                    } else {
                        trainingDTO.setTrainingType("Unknown");
                    }
                    trainingDTO.setTrainingDuration(training.getTrainingDuration());
                    if (training.getTrainer() != null && training.getTrainer().getUser()!= null) {
                        trainingDTO.setTrainerName(training.getTrainer().getUser().getFirstName() + " " +
                                training.getTrainer().getUser().getLastName());
                    } else {
                        trainingDTO.setTrainerName("Unknown");
                    }
                    return trainingDTO;
                })
                .collect(Collectors.toList());
    }
}


