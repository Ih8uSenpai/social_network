package com.example.social_network.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    private String username;
    private String passwordHash;
    private String email;
    private String firstName;
    private String lastName;
    private String tag;

}
