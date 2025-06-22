package com.gbroche.courseorganizer.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import com.gbroche.courseorganizer.model.Promo;

public class PromoDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Set<UserDTO> team;
    private Set<StudentDTO> students;

    public PromoDTO(Promo promo) {
        this.id = promo.getId();
        this.name = promo.getName();
        this.description = promo.getDescription();
        this.startDate = promo.getStartDate();
        this.endDate = promo.getEndDate();
        this.status = promo.getStatus().getLabel();
        this.team = promo.getUsers().stream().map(UserDTO::new).collect(Collectors.toSet());
        this.students = promo.getStudents().stream().map(StudentDTO::new).collect(Collectors.toSet());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate value) {
        this.startDate = value;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate value) {
        this.endDate = value;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public Set<StudentDTO> getStudents() {
        return this.students;
    }

    public void setStudents(Set<StudentDTO> students) {
        this.students = students;
    }

    public Set<UserDTO> getTeam() {
        return this.team;
    }

    public void setTeam(Set<UserDTO> users) {
        this.team = users;
    }
}
