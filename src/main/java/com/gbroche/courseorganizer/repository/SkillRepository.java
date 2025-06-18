package com.gbroche.courseorganizer.repository;

import java.util.List;

import com.gbroche.courseorganizer.model.Skill;

public interface SkillRepository extends RecordStatusRepository<Skill, Long> {
    List<Skill> findByIsHardSkillTrue();

    List<Skill> findByIsHardSkillFalse();

    boolean existsByLabel(String label);

}
