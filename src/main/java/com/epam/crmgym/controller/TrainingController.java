package com.epam.crmgym.controller;

import com.epam.crmgym.dto.training.TrainingRequest;
import com.epam.crmgym.service.TrainingService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("api/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private final MeterRegistry meterRegistry;



    @Autowired
    public TrainingController(TrainingService trainingService,MeterRegistry meterRegistry) {
        this.trainingService = trainingService;
        this.meterRegistry = meterRegistry;

    }

    @PostMapping("/create")
    public ResponseEntity<String> addTraining(@Validated @RequestBody TrainingRequest trainingRequest) {
        Counter requestsCounter = Counter.builder("training_requests_total")
                .description("Total number of training creation requests")
                .register(meterRegistry);

        try {
            trainingService.createTraining(trainingRequest.getTraineeUsername(), trainingRequest.getTrainerUsername(), trainingRequest.getTrainingName(), trainingRequest.getTrainingDate(), trainingRequest.getTrainingDuration(), trainingRequest.getPassword());
            requestsCounter.increment();
            return ResponseEntity.ok("Training created successfully!");
        } catch (Exception e) {
            requestsCounter.increment();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create training: " + e.getMessage());
        }
    }


}
