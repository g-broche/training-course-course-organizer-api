package com.gbroche.courseorganizer.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbroche.courseorganizer.model.Skill;
import com.gbroche.courseorganizer.repository.SkillRepository;

@RestController
@RequestMapping("api/skills")
public class SkillController {
    private final SkillRepository repository;

    public SkillController(SkillRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Skill> getAll() {
        return repository.findAll();
    }

    @GetMapping("/hard")
    public List<Skill> getAllHardSkills() {
        return repository.findByIsHardSkillTrue();
    }

    @GetMapping("/soft")
    public List<Skill> getAllSoftSkills() {
        return repository.findByIsHardSkillFalse();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Skill foundSkill = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(foundSkill);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No corresponding entity found");
        }
    }

    @PostMapping
    public Skill create(@RequestBody Skill skill) {
        skill.setCreatedAt(LocalDateTime.now());
        return repository.save(skill);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Skill skill) {
        try {
            Skill found = repository.findById(id).orElseThrow();
            found.setLabel(skill.getLabel());
            found.setIsHardSkill(skill.isHardSkill());
            found.setUpdatedAt(LocalDateTime.now());
            Skill editedSkill = repository.save(found);
            return ResponseEntity.ok(editedSkill);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("No corresponding entity found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update entity");
        }
    }
}
