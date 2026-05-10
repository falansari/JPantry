package com.ga.jpantry.models.requests;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String token;
    private String password;
    private String confirmPassword;
}
