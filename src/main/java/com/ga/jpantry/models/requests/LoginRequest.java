package com.ga.jpantry.models.requests;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}
