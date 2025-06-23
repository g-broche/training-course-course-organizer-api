package com.gbroche.courseorganizer.repository;

import com.gbroche.courseorganizer.model.Brief;
import com.gbroche.courseorganizer.model.Genre;

import java.util.Optional;

public interface BriefRepository extends RecordStatusRepository<Brief, Long> {
    Optional<Brief> findByName(String label);

    boolean existsByName(String name);
}
