package com.gbroche.courseorganizer.dto.user;

public class CreateUserRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String rawPassword;
    private Long genreId;

    public CreateUserRequestDTO(String firstName, String lastName, String email, String rawPassword, Long genreId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.rawPassword = rawPassword;
        this.genreId = genreId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public Long getGenreId() {
        return genreId;
    }
}
