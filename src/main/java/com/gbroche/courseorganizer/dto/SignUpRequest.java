package com.gbroche.courseorganizer.dto;

public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String rawPassword;
    private Long genreId;

    public SignUpRequest() {
    }

    public SignUpRequest(String firstName, String lastName, String email, String rawPassword, Long genreId) {
        System.out.println("REQUEST CONSTRUCTOR, name : " + firstName);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.rawPassword = rawPassword;
        this.genreId = genreId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        System.out.println("SET FIRST NAME CALLED with: " + firstName);
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

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }
}
