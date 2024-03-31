package com.epam.crmgym.controller;



import com.epam.crmgym.dto.trainee.TraineeUpdateRequest;
import com.epam.crmgym.dto.user.LoginRequest;
import com.epam.crmgym.exception.BindingResultError;
import com.epam.crmgym.exception.UsernameValidationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.epam.crmgym.dto.trainee.TraineeProfileDTO;
import com.epam.crmgym.dto.trainee.TraineeRegistrationDTO;
import com.epam.crmgym.dto.trainee.TraineeUpdateDTO;
import com.epam.crmgym.dto.trainer.TrainerResponse;
import com.epam.crmgym.dto.training.TrainingDTO;
import com.epam.crmgym.dto.user.UserCredentialsDTO;
import com.epam.crmgym.entity.Trainee;
import com.epam.crmgym.exception.AuthenticationException;
import com.epam.crmgym.mapper.TraineeMapper;
import com.epam.crmgym.service.AuthenticateService;
import com.epam.crmgym.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@Slf4j
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;

    private final AuthenticateService authenticateService;

    private final TraineeMapper traineeMapper;

    private final BindingResultError bindingResultError;


    @Autowired
    public TraineeController(TraineeService traineeService,
                             AuthenticateService authenticateService,
                             TraineeMapper traineeMapper, BindingResultError bindingResultError) {
        this.traineeService = traineeService;
        this.authenticateService = authenticateService;
        this.traineeMapper = traineeMapper;
        this.bindingResultError = bindingResultError;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerTrainee(@Validated @RequestBody TraineeRegistrationDTO registrationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> validationErrors = bindingResultError.handleBindingResultErrors(bindingResult);

            log.error("Validation errors occurred while processing /api/trainees/register endpoint: {}", validationErrors);

            return ResponseEntity.badRequest().body(validationErrors);
        }



        log.info("REST call made to /api/trainees/register endpoint. Request: {}", registrationDTO);

        try {
            Trainee createdTrainee = traineeService.createTrainee(
                    registrationDTO.getFirstName(),
                    registrationDTO.getLastName(),
                    registrationDTO.getDateOfBirth(),
                    registrationDTO.getAddress()
            );

            UserCredentialsDTO credentials = new UserCredentialsDTO();
            credentials.setUsername(createdTrainee.getUser().getUsername());
            credentials.setPassword(createdTrainee.getUser().getPassword());

            log.info("Successfully created trainee. Response: {}", credentials);

            return new ResponseEntity<>(credentials, HttpStatus.CREATED);
        } catch (UsernameValidationException e) {
            log.error("Error occurred while processing /api/trainees/register endpoint: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while processing /api/trainees/register endpoint", e);
            return new ResponseEntity<>("An error occurred while processing the request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping("/get-profile")
    public ResponseEntity<?> getTraineeProfile(@Validated @RequestBody UserCredentialsDTO userCredentials) {
        String username = userCredentials.getUsername();
        String password = userCredentials.getPassword();

        log.info("REST call made to /api/trainees/get-profile endpoint. Request: {} {}", username, password);

        try {
            boolean isAuthenticated = authenticateService.matchUserCredentials(username, password);
            log.info("User Authenticated Successfully");

            if (isAuthenticated) {
                TraineeProfileDTO profileDTO = traineeService.getTraineeProfile(username);
                return ResponseEntity.ok(profileDTO);
            } else {
                log.error("Authentication failed for user: {}", username);
                throw new AuthenticationException("Invalid username or password");
            }
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            log.info("Error occurred while processing /api/trainees/register endpoint.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }



    @PutMapping("/update-profile")
    public ResponseEntity<?> updateTraineeProfile(@Validated @RequestBody TraineeUpdateDTO updateDTO) {
        String username = updateDTO.getUsername();
        String password = updateDTO.getPassword();


        log.info("REST call made to /api/trainees/update-profile endpoint. Request: {} {} {}", username, password, updateDTO);

        try {
            if (!authenticateService.matchUserCredentials(username, password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            Trainee updatedTrainee = traineeService.updateTraineeProfile(
                    username,
                    updateDTO.getFirstName(),
                    password,
                    updateDTO.getLastName(),
                    updateDTO.getDateOfBirth(),
                    updateDTO.getAddress(),
                    updateDTO.getIsActive()
            );

            return updatedTrainee != null ?
                    ResponseEntity.ok(traineeMapper.mapTraineeToDTO(updatedTrainee)) :
                    ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.info("Error occurred while processing /api/trainees/update-profile endpoint.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }



    @DeleteMapping("/delete-profile")
    public ResponseEntity<?> deleteTraineeProfile(@Validated @RequestBody UserCredentialsDTO userCredentials) {
        String username = userCredentials.getUsername();
        String password = userCredentials.getPassword();

        log.info("REST call made to /api/trainees/delete-profile endpoint. Request: {} {}", username, password);

        try {

        if (!authenticateService.matchUserCredentials(username, password)) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        traineeService.deleteTraineeByUsername(username);

        return new ResponseEntity<>("Trainee profile deleted successfully", HttpStatus.OK);

        } catch (Exception e) {
            log.info("\"Error occurred while processing /api/trainees/delete-profile endpoint.\", e");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }

    @PatchMapping("/update-trainee-status")
    public ResponseEntity<String> updateTrainerStatus(@RequestParam String username,
                                                      @RequestParam boolean isActive) {
        traineeService.updateTraineeStatus(username, isActive);
        return ResponseEntity.ok("Trainer status updated successfully!");
    }

    @GetMapping("/trainings")
    public ResponseEntity<List<TrainingDTO>> getTraineeTrainingsList(
            @RequestParam String username,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingTypeName,
            @RequestParam String password) {

        try {
            log.info("REST call made to /api/trainees/trainings endpoint. Request: {} {}", username, password);

            List<TrainingDTO> trainings = traineeService.getTraineeTrainingsList(username, password, fromDate, toDate, trainerName, trainingTypeName);

            if (trainings == null || trainings.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(trainings);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing /api/trainees/trainings endpoint.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(new TrainingDTO("Error occurred while processing the request. Please try again later.", null, null, null, null)));
        }
    }



    @PutMapping("/{traineeUsername}/trainers")
    public ResponseEntity<List<TrainerResponse>> updateTraineeTrainerList(
            @PathVariable String traineeUsername,
            @RequestBody List<String> trainerUsernames) {
        log.info("Received request to update trainer list for trainee: {}", traineeUsername);

        List<TrainerResponse> updatedTrainers = traineeService.updateTraineeTrainerList(traineeUsername, trainerUsernames);
        if (updatedTrainers != null) {
            log.info("Trainer list updated successfully for trainee: {}", traineeUsername);
            return ResponseEntity.ok(updatedTrainers);
        } else {
            log.warn("Trainee not found with username: {}", traineeUsername);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}