package com.skillswap.repository;

import com.skillswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Пример кастомного запроса
    @Query("SELECT u FROM User u WHERE u.role = :role")
    java.util.List<User> findAllByRole(String role);
} 