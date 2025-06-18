package com.gbroche.courseorganizer.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.model.User;

public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String genre;
    private Set<String> roles;

    public UserDTO(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.genre = user.getGenre().getLabel();
        this.roles = user.getRoles().stream().map(Role::getLabel)
                .collect(Collectors.toSet());
    }

    // getters/setters

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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
