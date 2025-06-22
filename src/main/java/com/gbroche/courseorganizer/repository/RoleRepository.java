package com.gbroche.courseorganizer.repository;

import com.gbroche.courseorganizer.model.Role;

import java.util.Optional;

public interface RoleRepository extends RecordStatusRepository<Role, Long> {
    Optional<Role> findByLabel(String label);

    boolean existsByLabel(String label);
}
