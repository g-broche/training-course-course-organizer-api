package com.gbroche.courseorganizer.repository;

import java.util.Optional;

import com.gbroche.courseorganizer.model.User;

public interface UserRepository extends RecordStatusRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
