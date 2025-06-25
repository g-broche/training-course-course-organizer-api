package com.gbroche.courseorganizer.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.gbroche.courseorganizer.model.Student;

public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String genre;
    private Boolean hasDoneDwwm;
    private LocalDate birthdate;
    private Set<PromoDTO> promos;

    public StudentDTO(Student student) {
        this.id = student.getId();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.email = student.getEmail();
        this.genre = student.getGenre().getLabel();
        this.birthdate = student.getBirthdate();
        this.hasDoneDwwm = student.hasDoneDwwm();
        this.promos = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean hasDoneDwwm() {
        return hasDoneDwwm;
    }

    public void setHasDoneDwwm(Boolean value) {
        this.hasDoneDwwm = value;
    }
}
