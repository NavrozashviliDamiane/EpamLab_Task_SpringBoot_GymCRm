package com.epam.crmgym.controller;

import com.epam.crmgym.dto.training.TrainingTypeDTO;
import com.epam.crmgym.entity.TrainingType;
import com.epam.crmgym.mapper.TrainingTypeMapper;
import com.epam.crmgym.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeMapper trainingTypeMapper;

    @Autowired
    public TrainingTypeController(TrainingTypeService trainingTypeService, TrainingTypeMapper trainingTypeMapper) {
        this.trainingTypeService = trainingTypeService;
        this.trainingTypeMapper = trainingTypeMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TrainingTypeDTO>> getTrainingTypes() {
        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();
        List<TrainingTypeDTO> trainingTypeDTOs = trainingTypes.stream()
                .map(trainingTypeMapper::mapToTrainingTypeDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(trainingTypeDTOs);
    }
}

