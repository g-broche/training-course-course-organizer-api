package com.gbroche.courseorganizer.controller;

import com.gbroche.courseorganizer.model.Student;
import com.gbroche.courseorganizer.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.repository.GenreRepository;
import com.gbroche.courseorganizer.repository.RoleRepository;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
abstract public class PersonBasedTester {

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected GenreRepository genreRepository;

    @BeforeAll
    void setupTestData() {
        roleRepository.save(new Role("ADMIN"));
        roleRepository.save(new Role("TEACHER"));
        roleRepository.save(new Role("USER"));

        genreRepository.save(new Genre("Female"));
        genreRepository.save(new Genre("Male"));
        genreRepository.save(new Genre("Non binary"));
    }

    protected Student createTestStudent(String firstName, String lastName, Genre genre, LocalDate birthdate) {
        String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.test";
        Student testStudent = new Student(firstName, lastName, email);
        testStudent.setGenre(genre);
        if(birthdate != null){
            testStudent.setBirthdate(birthdate);
        }else{
        testStudent.setBirthdate(LocalDate.of(
                ThreadLocalRandom.current().nextInt(1970, 2009),
                ThreadLocalRandom.current().nextInt(1, 12),
                ThreadLocalRandom.current().nextInt(1, 28)
                )
        );
        }
        return testStudent;
    }

    protected User createTestUser(String firstName, String lastName, Genre genre, Set<Role> roles) {
        String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.test";
        User testUser = new User(firstName, lastName, email);
        testUser.setPassword("testpassword");
        testUser.setRoles(roles);
        testUser.setGenre(genre);
        return testUser;
    }
}
