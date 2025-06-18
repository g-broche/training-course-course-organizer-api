package com.gbroche.courseorganizer.repository;

import com.gbroche.courseorganizer.model.Student;

public interface StudentRepository extends RecordStatusRepository<Student, Long> {
    boolean existsByEmail(String email);
}
