package com.example.ByteBites.repository;

import com.example.ByteBites.models.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findByUsername(String username);
    Optional<Accounts> findByEmail(String email);
    Optional<Accounts> findByUsernameIgnoreCase(String username);
}
