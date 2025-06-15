package com.gbroche.courseorganizer.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.dto.user.CreateUserRequestDTO;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.model.User;
import com.gbroche.courseorganizer.repository.GenreRepository;
import com.gbroche.courseorganizer.repository.RoleRepository;
import com.gbroche.courseorganizer.repository.StatusRepository;
import com.gbroche.courseorganizer.repository.UserRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUpRelations() {
        roleRepository.save(new Role("ADMIN"));
        roleRepository.save(new Role("TEACHER"));
        roleRepository.save(new Role("USER"));

        genreRepository.save(new Genre("Female"));
        genreRepository.save(new Genre("Male"));
        genreRepository.save(new Genre("Non binary"));
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @AfterAll
    void clearRelations() {
        repository.deleteAll();
        roleRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    void testCreate_Should_CreateNewUser() throws Exception {
        CreateUserRequestDTO toAdd = new CreateUserRequestDTO(
                "John",
                "Doe",
                "John.doe@test.Test",
                "testuser",
                (long) 2);
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toAdd)))
                .andExpect(status().isOk())
                .andExpect(content().string("User John Doe was created."));
    }
}

// @Test
// void testChangeUserRoles() {
// assertTrue(false);
// }

// @Test
// void testGetAll() {
// assertTrue(false);
// }

// @Test
// void testGetById() {
// assertTrue(false);
// }

// @Test
// void testSoftDeleteById() {
// assertTrue(false);
// }