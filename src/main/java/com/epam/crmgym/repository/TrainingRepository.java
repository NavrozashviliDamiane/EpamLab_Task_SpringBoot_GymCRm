package com.epam.crmgym.repository;

import com.epam.crmgym.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByTraineeId(Long traineeId);

    List<Training> findByTrainerId(Long trainerId);


    List<Training> findByTraineeIdAndTrainingDateBetweenAndTrainerIdAndTrainingTypeId(
            Long traineeId, Date fromDate, Date toDate, Long trainerId, Long trainingTypeId);

    List<Training> findByTrainerIdAndTrainingDateBetweenAndTraineeId(
            Long traineeId, Date fromDate, Date toDate, Long trainerId);


    List<Training> findByTraineeIdAndTrainingDateBetweenAndTrainingTypeId(Long traineeId, Date fromDate, Date toDate, Long trainingTypeId);

    List<Training> findByTraineeIdAndTrainingDateBetween(Long traineeId, Date fromDate, Date toDate);

    List<Training> findByTrainerUserUsernameAndTrainingDateBetween(String username, Date periodFrom, Date periodTo);

    List<Training> findByTrainerUserUsername(String username);

    List<Training> findByTrainerUserUsernameAndTrainingDateBetweenAndTraineeUserFirstNameContainingIgnoreCase(String username, Date periodFrom, Date periodTo, String traineeName);

    List<Training> findByTrainerUserUsernameAndTraineeUserFirstNameContainingIgnoreCase(String username, String traineeName);

    List<Training> findByTraineeIdAndTrainerIdAndTrainingTypeId(Long traineeId, Long trainerId, Long trainingTypeId);

    List<Training> findByTraineeIdAndTrainerId(Long traineeId, Long trainerId);

    List<Training> findByTraineeIdAndTrainingTypeId(Long traineeId, Long trainingTypeId);

    List<Training> findByTrainerIdAndTraineeId(Long trainerId, Long traineeId);

    List<Training> findByTrainerIdAndTrainingDateBetween(Long trainerId, Date fromDate, Date toDate);

    List<Training> findByTrainerIdAndTrainingDateAfter(Long trainerId, Date fromDate);

    List<Training> findByTrainerIdAndTrainingDateBefore(Long trainerId, Date toDate);

    List<Training> findByTraineeIdAndTrainingDateBetweenAndTrainerId(Long traineeId, Date fromDate, Date toDate, Long trainerId);

    List<Training> findByTraineeIdAndTrainingDateBeforeAndTrainerIdAndTrainingTypeId(Long traineeId, Date toDate, Long trainerId, Long trainingTypeId);

    List<Training> findByTraineeIdAndTrainingDateBeforeAndTrainerId(Long traineeId, Date toDate, Long trainerId);

    List<Training> findByTraineeIdAndTrainingDateBeforeAndTrainingTypeId(Long traineeId, Date toDate, Long trainingTypeId);

    List<Training> findByTraineeIdAndTrainingDateBefore(Long traineeId, Date toDate);

    List<Training> findByTraineeIdAndTrainingDateAfterAndTrainerIdAndTrainingTypeId(Long traineeId, Date toDate, Long trainerId, Long trainingTypeId);

    List<Training> findByTraineeIdAndTrainingDateAfterAndTrainerId(Long traineeId, Date toDate, Long trainerId);

    List<Training> findByTraineeIdAndTrainingDateAfterAndTrainingTypeId(Long traineeId, Date toDate, Long trainingTypeId);

    List<Training> findByTraineeIdAndTrainingDateAfter(Long traineeId, Date toDate);

    List<Training> findByTraineeIdAndTrainingDateBetweenAndTrainerIdAndTrainingTypeIdAndTrainingDurationGreaterThan(Long traineeId, Date fromDate, Date toDate, Long trainerId, Long trainingTypeId, int i);

    List<Training> findByTraineeIdAndTrainingDateBetweenAndTrainerIdAndTrainingDurationGreaterThan(Long traineeId, Date fromDate, Date toDate, Long trainerId, int i);

    List<Training> findByTraineeIdAndTrainingDateBetweenAndTrainingTypeIdAndTrainingDurationGreaterThan(Long traineeId, Date fromDate, Date toDate, Long trainingTypeId, int i);

    List<Training> findByTraineeIdAndTrainingDateBetweenAndTrainingDurationGreaterThan(Long traineeId, Date fromDate, Date toDate, int i);

    List<Training> findByTraineeIdAndTrainingDateAfterAndTrainerIdAndTrainingTypeIdAndTrainingDurationGreaterThan(Long traineeId, Date toDate, Long trainerId, Long trainingTypeId, int i);

    List<Training> findByTraineeIdAndTrainingDateAfterAndTrainerIdAndTrainingDurationGreaterThan(Long traineeId, Date toDate, Long trainerId, int i);

    List<Training> findByTraineeIdAndTrainingDateAfterAndTrainingTypeIdAndTrainingDurationGreaterThan(Long traineeId, Date toDate, Long trainingTypeId, int i);

    List<Training> findByTraineeIdAndTrainingDateAfterAndTrainingDurationGreaterThan(Long traineeId, Date toDate, int i);

    List<Training> findByTraineeIdAndTrainingDateGreaterThanEqualAndTrainerIdAndTrainingTypeIdAndTrainingDurationGreaterThan(Long traineeId, Date fromDate, Long trainerId, Long trainingTypeId, int i);

    List<Training> findByTraineeIdAndTrainingDateGreaterThanEqualAndTrainerIdAndTrainingDurationGreaterThan(Long traineeId, Date fromDate, Long trainerId, int i);

    List<Training> findByTraineeIdAndTrainingDateGreaterThanEqualAndTrainingTypeIdAndTrainingDurationGreaterThan(Long traineeId, Date fromDate, Long trainingTypeId, int i);

    List<Training> findByTraineeIdAndTrainingDateGreaterThanEqualAndTrainingDurationGreaterThan(Long traineeId, Date fromDate, int i);
}
