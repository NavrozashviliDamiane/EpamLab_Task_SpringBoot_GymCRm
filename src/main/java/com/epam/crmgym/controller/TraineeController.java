package com.epam.crmgym.controller;


import com.epam.crmgym.dto.trainee.*;
import com.epam.crmgym.dto.user.UpdateUserStatusRequestDTO;
import com.epam.crmgym.exception.*;
import com.epam.crmgym.repository.TraineeRepository;
import lombok.extern.slf4j.Slf4j;
import com.epam.crmgym.dto.trainer.TrainerResponse;
import com.epam.crmgym.dto.training.TrainingDTO;
import com.epam.crmgym.dto.user.UserCredentialsDTO;
import com.epam.crmgym.entity.Trainee;
import com.epam.crmgym.mapper.TraineeMapper;
import com.epam.crmgym.service.AuthenticateService;
import com.epam.crmgym.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;

    private final AuthenticateService authenticateService;

    private final TraineeMapper traineeMapper;

    private final BindingResultError bindingResultError;

    private final TraineeRepository traineeRepository;


    @Autowired
    public TraineeController(TraineeService traineeService,
                             AuthenticateService authenticateService,
                             TraineeMapper traineeMapper, BindingResultError bindingResultError,
                             TraineeRepository traineeRepository) {
        this.traineeService = traineeService;
        this.authenticateService = authenticateService;
        this.traineeMapper = traineeMapper;
        this.bindingResultError = bindingResultError;
        this.traineeRepository = traineeRepository;
    }

    @PostMapping
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




    @GetMapping
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



    @PutMapping
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



    @DeleteMapping
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

    @PatchMapping
    public ResponseEntity<String> updateTraineeStatus(@Validated @RequestBody UpdateUserStatusRequestDTO requestDTO) {
        boolean authenticated = authenticateService.matchUserCredentials(requestDTO.getUsername(), requestDTO.getPassword());
        if (!authenticated) {
            log.error("User authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed: User credentials do not match");
        }

        log.info("User authenticated successfully");

        Trainee trainee = traineeRepository.findByUserUsername(requestDTO.getUsername());
        if (trainee == null) {
            log.error("Trainee not found with username: " + requestDTO.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Trainee not found with username: " + requestDTO.getUsername());
        }

        try {
            traineeService.updateTraineeStatus(requestDTO.getUsername(), requestDTO.getIsActive());
            return ResponseEntity.ok("Trainee status updated successfully!");
        } catch (Exception e) {
            log.error("An error occurred while updating trainee status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while updating trainee status");
        }
    }



    @GetMapping("trainings-list")
    public ResponseEntity<?> getTraineeTrainingsList(@Validated @RequestBody TraineeTrainingsRequestDTO requestDTO) {
        try {
            log.info("REST call made to /api/trainees/trainings endpoint. Request: {} {}", requestDTO.getUsername(), requestDTO.getPassword());

            List<TrainingDTO> trainings = traineeService.getTraineeTrainingsList(
                    requestDTO.getUsername(),
                    requestDTO.getPassword(),
                    requestDTO.getFromDate(),
                    requestDTO.getToDate(),
                    requestDTO.getTrainerName(),
                    requestDTO.getTrainingTypeName()
            );

            if (trainings == null || trainings.isEmpty()) {
                String message = "No trainings found for the specified criteria.";
                return ResponseEntity.ok().body(Collections.singletonMap("message", message));
            } else {
                return ResponseEntity.ok(trainings);
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            log.error("Error occurred while processing /api/trainees/trainings endpoint.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(new TrainingDTO("Error occurred while processing the request. Please try again later.", null, null, null, null)));
        }
    }


    @PutMapping("/trainer-list")
    public ResponseEntity<?> updateTraineeTrainerList(@Validated @RequestBody UpdateTraineeTrainerListRequestDTO requestDTO) {
        String traineeUsername = requestDTO.getTraineeUsername();
        String password = requestDTO.getPassword();

        boolean isAuthenticated = authenticateService.matchUserCredentials(traineeUsername, password);
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Authentication failed, please provide valid credentials"));
        }

        try {
            log.info("REST call made to /api/trainees/trainers endpoint.");

            List<TrainerResponse> updatedTrainers = traineeService.updateTraineeTrainersList(
                    traineeUsername,
                    requestDTO.getTrainerUsernames()
            );

            if (updatedTrainers == null || updatedTrainers.isEmpty()) {
                String message = "No trainers found or updated for the specified trainee.";
                return ResponseEntity.ok().body(Collections.singletonMap("message", message));
            } else {
                return ResponseEntity.ok(updatedTrainers);
            }
        } catch (TraineeNotFoundException e) {
            log.error("Trainee not found with username: {}", traineeUsername, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Trainee not found with username: " + traineeUsername));
        } catch (InvalidTrainersException e) {
            log.error("Number of trainers provided exceeds the number of available trainings for trainee: {}", traineeUsername, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Number of trainers provided exceeds the number of available trainings"));
        } catch (TrainerNotFoundException e) {
            log.error("Trainer not found with username: {}",  e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Trainer not found from the provided list " ));
        }   catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while processing /api/trainees/trainers endpoint.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error occurred while processing the request."));
        }
    }
}
