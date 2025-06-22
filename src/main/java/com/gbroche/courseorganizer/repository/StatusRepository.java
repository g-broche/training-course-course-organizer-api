package com.gbroche.courseorganizer.repository;

import com.gbroche.courseorganizer.model.Status;

import java.util.Optional;

public interface StatusRepository extends RecordStatusRepository<Status, Long> {
    boolean existsByLabel(String label);
    Optional<Status> findByLabel(String label);
}
