package com.gbroche.courseorganizer.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"group\"")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "brief_id")
    private Brief brief;

    @ManyToMany
    @JoinTable(
            name = "group_student",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students = new HashSet<>();

    public Group(String name, Brief brief, Set<Student> students) {
        this.name = name;
        this.setBrief(brief);
        for (Student student : students){
            this.students.add(student);
//            student.addGroup(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return name;
    }

    public void setLabel(String label) {
        this.name = label;
    }

    public Brief getBrief() {
        return brief;
    }

    public void setBrief(Brief brief) {
        this.brief = brief;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student){
        if (!students.contains(student)) {
            students.add(student);
            student.addGroup(this);
        }
    }

    public void removeStudent(Student student){
        if (students.contains(student)) {
            students.remove(student);
            student.removeGroup(this);
        }
    }
}
