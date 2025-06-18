package com.gbroche.courseorganizer.repository;

import com.gbroche.courseorganizer.model.Status;

public interface StatusRepository extends RecordStatusRepository<Status, Long> {
    boolean existsByLabel(String label);
}
