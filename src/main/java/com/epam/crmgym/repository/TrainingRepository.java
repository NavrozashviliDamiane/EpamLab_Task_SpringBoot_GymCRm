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
}
