package com.ga.jpantry.controllers;

import com.ga.jpantry.models.Verification;
import com.ga.jpantry.services.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/users/verify")
    public ResponseEntity<String> verify(@RequestParam("token") String token) {
        boolean isVerified = verificationService.verifyEmailToken(token);

        if (isVerified) {
            return ResponseEntity.ok("Account is verified. You can now login.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }

    @PostMapping("/users/token")
    public Verification reissueToken(@RequestParam String token) {
        return verificationService.reissueVerificationToken(token);
    }
}
