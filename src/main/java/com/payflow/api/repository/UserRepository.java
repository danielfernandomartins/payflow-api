package com.payflow.api.repository;

import com.payflow.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByDocument(String document);
    Optional<User> findByEmail(String email);
    boolean existsByDocument(String document);
    boolean existsByEmail(String email);
}
