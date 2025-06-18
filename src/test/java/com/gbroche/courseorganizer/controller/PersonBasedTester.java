package com.gbroche.courseorganizer.controller;

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
}
