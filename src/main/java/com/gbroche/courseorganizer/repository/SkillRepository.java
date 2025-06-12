package com.gbroche.courseorganizer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gbroche.courseorganizer.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByIsHardSkillTrue();

    List<Skill> findByIsHardSkillFalse();
}
