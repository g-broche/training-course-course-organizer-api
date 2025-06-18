package com.gbroche.courseorganizer.repository;

import com.gbroche.courseorganizer.model.Role;

public interface RoleRepository extends RecordStatusRepository<Role, Long> {
    Role findByLabel(String label);

    boolean existsByLabel(String label);
}
