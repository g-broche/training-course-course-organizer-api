package com.gbroche.courseorganizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gbroche.courseorganizer.model.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}
