package com.hackathon.project.tracker.repository;

import com.hackathon.project.tracker.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity>findByEmail(String email);
    Boolean existsByEmail(String email);
}
