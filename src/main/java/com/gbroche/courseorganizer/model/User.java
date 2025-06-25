package com.gbroche.courseorganizer.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User extends Person {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "has_accepted_terms", nullable = false)
    private boolean hasAcceptedTerms = false;

    @Column(name = "has_accepted_cookies", nullable = false)
    private boolean hasAcceptedCookies = false;

    @Column(name = "answered_terms_at")
    private LocalDateTime answeredTermsAt;

    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_promo", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "promo_id"))
    private Set<Promo> promos = new HashSet<>();

    public User() {
        super();
    }

    public User(String firstName, String lastName, String email) {
        super();
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Promo> getPromos() {
        return  promos;
    }

    public void addPromo(Promo promo) {
        if(!promos.contains(promo)) {
            promos.add(promo);
            promo.getUsers().add(this);
        }
    }

    public void removePromo(Promo promo) {
        if(promos.contains(promo)) {
            promos.remove(promo);
            promo.getUsers().remove(this);
        }
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public boolean hasAcceptedTerms() {
        return hasAcceptedTerms;
    }

    public void setHasAcceptedTerms(boolean hasAcceptedTerms) {
        this.hasAcceptedTerms = hasAcceptedTerms;
    }

    public boolean hasAcceptedCookies() {
        return hasAcceptedCookies;
    }

    public void setHasAcceptedCookies(boolean hasAcceptedCookies) {
        this.hasAcceptedCookies = hasAcceptedCookies;
    }

    public LocalDateTime getAnsweredTermsAt() {
        return answeredTermsAt;
    }

    public void setAnsweredTermsAt(LocalDateTime answeredTermsAt) {
        this.answeredTermsAt = answeredTermsAt;
    }
}
