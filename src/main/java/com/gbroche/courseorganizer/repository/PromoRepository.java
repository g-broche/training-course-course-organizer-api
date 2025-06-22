package com.gbroche.courseorganizer.repository;

import java.util.Optional;

import com.gbroche.courseorganizer.model.Promo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface PromoRepository extends RecordStatusRepository<Promo, Long> {
//    @EntityGraph(attributePaths = {"students"})
//    @Query("SELECT p FROM Promo p WHERE p.id = :id")
    @Query("SELECT p FROM Promo p LEFT JOIN FETCH p.students WHERE p.id = :id")
    Optional<Promo> findWithStudentsById(@Param("id") Long id);

    Optional<Promo> findByName(String name);
}
