package com.epam.crmgym.util.user;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return password.toString();
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return new String(salt);
    }

    public static String hashPasswordWithSalt(String password, String salt) {
        String saltedPassword = password + salt;
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10); // Specify the cost parameter as 10
        return passwordEncoder.encode(saltedPassword);
    }
}

