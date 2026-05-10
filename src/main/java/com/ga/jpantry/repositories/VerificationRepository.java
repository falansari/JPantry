package com.ga.jpantry.repositories;

import com.ga.jpantry.models.User;
import com.ga.jpantry.models.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
    Verification findByToken(String token);
    User getUserByToken(String token);
}
