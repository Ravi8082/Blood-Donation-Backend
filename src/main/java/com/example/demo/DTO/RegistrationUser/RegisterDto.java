package com.example.demo.DTO.RegistrationUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private String name;
    private String email;
    private String mobile;
    private String age;
    private String gender;
    private String bloodgroup;
    private String address;
    private String password;
    private String confirmPassword;
    private String Role;
}
