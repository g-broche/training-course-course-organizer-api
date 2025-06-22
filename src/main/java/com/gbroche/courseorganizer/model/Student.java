package com.gbroche.courseorganizer.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class Student extends Person {

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "has_done_dwwm", nullable = false)
    private boolean hasDoneDwwm = false;

    @ManyToMany
    @JoinTable(name = "student_promo", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "promo_id"))
    private Set<Promo> promos = new HashSet<>();

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

    public void addPromo(Promo promo) {
        promos.add(promo);
        promo.getStudents().add(this);
    }

    public Set<Promo> getPromos() {
        return promos;
    }
}
