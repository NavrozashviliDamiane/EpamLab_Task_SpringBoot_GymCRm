package com.epam.crmgym.controller;

import lombok.extern.slf4j.Slf4j;
import com.epam.crmgym.dto.user.ChangePasswordRequest;
import com.epam.crmgym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Validated @RequestBody ChangePasswordRequest request) {

        log.info("REST call made to /api/users/change-password endpoint. Request: {}", request);

        try {
            userService.changePassword(request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            log.info("\"Error occurred while processing /api/users/change-password endpoint.\", e");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
