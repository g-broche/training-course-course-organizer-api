package com.gbroche.courseorganizer.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Student extends Person {

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "has_done_dwwm", nullable = false)
    private boolean hasDoneDwwm = false;

    public Student() {
        super();
    }

    public Student(String firstName, String lastName, String email) {
        super();
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public boolean hasDoneDwwm() {
        return hasDoneDwwm;
    }

    public void setHasDoneDwwm(boolean hasDoneDwwm) {
        this.hasDoneDwwm = hasDoneDwwm;
    }
}
