package com.gbroche.courseorganizer.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String label;
    @Column(nullable = false)
    private boolean isHardSkill;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Skill() {
    }

    public Skill(String label, boolean isHardSkill) {
        this.label = label;
        this.isHardSkill = isHardSkill;
    }

    public Skill(String label, boolean isHardSkill, LocalDateTime createdAt) {
        this.label = label;
        this.isHardSkill = isHardSkill;
        this.createdAt = createdAt;
    }

    public Skill(String label, boolean isHardSkill, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.label = label;
        this.isHardSkill = isHardSkill;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isHardSkill() {
        return isHardSkill;
    }

    public void setIsHardSkill(boolean isHardSkill) {
        this.isHardSkill = isHardSkill;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
