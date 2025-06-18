package com.gbroche.courseorganizer.dto;

import java.time.LocalDate;

public class StudentRequest {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private Long genreId;

    public StudentRequest() {
    }

    public StudentRequest(String firstName, String lastName, String email, LocalDate birthDate, Long genreId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.genreId = genreId;
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

    public LocalDate getBirthdate() {
        return birthDate;
    }

    public void setBirthdate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }
}
