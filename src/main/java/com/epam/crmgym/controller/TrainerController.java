package com.epam.crmgym.controller;

import com.epam.crmgym.dto.user.UpdateUserStatusRequestDTO;
import com.epam.crmgym.dto.user.UserCredentialsDTO;
import com.epam.crmgym.exception.BindingResultError;
import com.epam.crmgym.exception.UsernameValidationException;
import com.epam.crmgym.repository.TrainerRepository;
import lombok.extern.slf4j.Slf4j;
import com.epam.crmgym.dto.trainer.*;
import com.epam.crmgym.entity.Trainer;
import com.epam.crmgym.service.AuthenticateService;
import com.epam.crmgym.service.TrainerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    private final AuthenticateService authenticateService;

    private final BindingResultError bindingResultError;

    private final TrainerRepository trainerRepository;

    public TrainerController(TrainerService trainerService,
                             AuthenticateService authenticateService,
                             BindingResultError bindingResultError, TrainerRepository trainerRepository) {
        this.trainerService = trainerService;
        this.authenticateService = authenticateService;
        this.bindingResultError = bindingResultError;
        this.trainerRepository = trainerRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerTrainer(@Validated @RequestBody TrainerRegistrationRequest request, BindingResult bindingResult) {
        log.info("REST call made to /api/trainers/register endpoint. Request: {}", request);

        if (bindingResult.hasErrors()) {
            List<String> validationErrors = bindingResultError.handleBindingResultErrors(bindingResult);

            log.error("Validation errors occurred while processing /api/trainers/register endpoint: {}", validationErrors);

            return ResponseEntity.badRequest().body(validationErrors);
        }

        try {
            Trainer trainer = trainerService.createTrainer(request);
            TrainerRegistrationResponse response = new TrainerRegistrationResponse(
                    trainer.getUser().getUsername(),
                    trainer.getUser().getPassword());

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (UsernameValidationException e) {
            log.error("Error occurred while processing /api/trainers/register endpoint: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            log.info("Error occurred while processing /api/trainers/register endpoint: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-profile")
    public ResponseEntity<?> getTrainerProfile(@Validated @RequestBody UserCredentialsDTO userCredentials) {
        String username = userCredentials.getUsername();
        String password = userCredentials.getPassword();

        log.info("REST call made to /api/trainers/get-profile endpoint. Request: {} {}", username, password);

        if (!authenticateService.matchUserCredentials(username, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        TrainerProfileDTO trainerProfile = trainerService.getTrainerProfile(username, password);
        if (trainerProfile != null) {
            return ResponseEntity.ok(trainerProfile);
        } else {
            log.info("Error occurred while processing /api/trainers/get-profile endpoint.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }
    }


    @PutMapping("/update-profile")
    public ResponseEntity<?> updateTrainerProfile(@Validated @RequestBody TrainerUpdateDTO trainerUpdateDTO) {

        String username = trainerUpdateDTO.getUsername();
        String password = trainerUpdateDTO.getPassword();

        log.info("REST call made to /api/trainers/update-profile endpoint. Request: {}", trainerUpdateDTO);

        if (!authenticateService.matchUserCredentials(username, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        TrainerProfileDTO updatedProfile = trainerService.updateTrainerProfile(trainerUpdateDTO);
        if (updatedProfile != null) {
            return ResponseEntity.ok(updatedProfile);
        } else {
            log.info("\"Error occurred while processing /api/trainers/update-profile endpoint.\", e");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }
    }

    @PatchMapping("/update-trainer-status")
    public ResponseEntity<String> updateTrainerStatus(@Validated @RequestBody UpdateUserStatusRequestDTO requestDTO) {
        boolean authenticated = authenticateService.matchUserCredentials(requestDTO.getUsername(), requestDTO.getPassword());
        if (!authenticated) {
            log.error("User authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed: User credentials do not match");
        }

        log.info("User authenticated successfully");

        Trainer trainer = trainerRepository.findByUserUsername(requestDTO.getUsername());
        if (trainer == null) {
            log.error("Trainer not found with username: " + requestDTO.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Trainer not found with username: " + requestDTO.getUsername());
        }

        try {
            trainerService.updateTrainerStatus(requestDTO.getUsername(), requestDTO.getIsActive());
            return ResponseEntity.ok("Trainer status updated successfully!");
        } catch (Exception e) {
            log.error("An error occurred while updating trainer status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while updating trainer status");
        }
    }


    @GetMapping("/unassigned-active")
    public ResponseEntity<?> getUnassignedActiveTrainersByTraineeUsername(
            @Validated @RequestBody UserCredentialsDTO userCredentials) {
        String traineeUsername = userCredentials.getUsername();
        String password = userCredentials.getPassword();

        log.info("REST call made to /api/trainers/unassigned-active endpoint. Request: {} {}", traineeUsername, password);

        if (!authenticateService.matchUserCredentials(traineeUsername, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonList("Invalid username or password"));
        }

        List<TrainerDTO> unassignedActiveTrainers = trainerService.findUnassignedActiveTrainersByTraineeUsername(traineeUsername, password);
        return ResponseEntity.ok(unassignedActiveTrainers);
    }


    @GetMapping("/trainer/trainings")
    public ResponseEntity<List<TrainerTrainingResponseDTO>> getTrainerTrainings(
            @RequestBody TrainerTrainingsRequestDTO request
    ) {
        String username = request.getUsername();
        String password = request.getPassword();

        log.info("REST call made to /api/trainers/trainings endpoint. Request: {} {}", username, password);
        if (!authenticateService.matchUserCredentials(username, password)) {
            return new ResponseEntity("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        try {
            List<TrainerTrainingResponseDTO> trainings = trainerService.getTrainerTrainings(request);
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            log.info("Error occurred while processing /api/trainers/trainings endpoint:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
