package com.gbroche.courseorganizer.repository;

import com.gbroche.courseorganizer.model.Student;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends RecordStatusRepository<Student, Long> {
    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"promos"})
    Optional<Student> findWithPromosById(Long id);

    @EntityGraph(attributePaths = {"promos"})
    List<Student> findWithPromosByIdIn(List<Long> ids);
}
