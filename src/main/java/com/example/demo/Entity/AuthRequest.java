package com.example.demo.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;

    public AuthRequest(String email, String password){
        super();
        this.email = email;
        this.password = password;
    }
}
