package com.gbroche.courseorganizer.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;

@Entity
@NamedEntityGraph(
        name = "Promo.withStudents",
        attributeNodes = @NamedAttributeNode("students")
)
public class Promo extends RecordStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @ManyToMany(mappedBy = "promos")
    private Set<User> users = new HashSet<>();

    @ManyToMany(mappedBy = "promos")
    private Set<Student> students = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now();
    }

    public Promo(){}
    public Promo(String name, String description, LocalDate startDate, LocalDate endDate){
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        if(!students.contains(student)){
            this.students.add(student);
            student.getPromos().add(this);
        }
    }

    public void removeStudent(Student student) {
        if(students.contains(student)){
            this.students.remove(student);
            student.getPromos().remove(this);
        }
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> newUsers) {
        Set<User> usersToRemove = new HashSet<>(this.users);
        for (User existingUser : usersToRemove) {
            if (!newUsers.contains(existingUser)) {
                this.users.remove(existingUser);
                existingUser.getPromos().remove(this);
            }
        }

        for (User newUser : newUsers){
            if(!this.users.contains(newUser)){
                this.users.add(newUser);
                newUser.getPromos().add(this);
            }
        }
    }

    public void addUser(User user) {
        if(!users.contains(user)){
            this.users.add(user);
            user.getPromos().add(this);
        }

    }

    public void removeUser(User user) {
        if(users.contains(user)){
            this.users.remove(user);
            user.getPromos().remove(this);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
}