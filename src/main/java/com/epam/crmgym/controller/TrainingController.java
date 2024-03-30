package com.epam.crmgym.controller;


import lombok.extern.slf4j.Slf4j;
import com.epam.crmgym.dto.training.TrainingRequest;
import com.epam.crmgym.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> addTraining(@RequestBody TrainingRequest trainingRequest) {
        trainingService.createTraining(
                trainingRequest.getTraineeUsername(),
                trainingRequest.getTrainerUsername(),
                trainingRequest.getTrainingName(),
                trainingRequest.getTrainingDate(),
                trainingRequest.getTrainingDuration(),
                trainingRequest.getPassword()
        );

        return ResponseEntity.ok().build();
    }

}
