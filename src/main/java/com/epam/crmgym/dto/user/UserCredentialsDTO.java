package com.epam.crmgym.dto.user;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCredentialsDTO {
    private String username;
    private String password;

}
