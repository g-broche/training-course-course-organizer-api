package com.gbroche.courseorganizer.repository;

import java.util.Optional;

import com.gbroche.courseorganizer.model.Genre;

public interface GenreRepository extends RecordStatusRepository<Genre, Long> {
    Optional<Genre> findByLabel(String label);

    boolean existsByLabel(String label);
}
