package com.ga.jpantry.repositories;

import com.ga.jpantry.models.User;
import com.ga.jpantry.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
    boolean existsByRole(Role role);
}
