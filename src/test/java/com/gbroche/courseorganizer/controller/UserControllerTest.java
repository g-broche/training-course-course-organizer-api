package com.gbroche.courseorganizer.controller;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.model.User;
import com.gbroche.courseorganizer.repository.UserRepository;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:usertest-${random.uuid};DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest extends PersonBasedTester {

    @Autowired
    private UserRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetAll_ShouldReturnAllUsers() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByLabel("USER").orElseThrow());
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        User testUser1 = createTestUser("John", "Doe", genre, roles);
        User testUser2 = createTestUser("Mathews", "Doe", genre, roles);
        repository.save(testUser1);
        repository.save(testUser2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetById_GivenValidIdShouldReturnUser() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByLabel("USER").orElseThrow());
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        User testUser = createTestUser("John", "Doe", genre, roles);
        User existingUser = repository.save(testUser);

        mockMvc.perform(get("/api/users/" + existingUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testChangeUserRoles_GivenValidSetOfRole_ReturnsUpdatedUser() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByLabel("USER").orElseThrow());
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        User testUser = createTestUser("John", "Doe", genre, roles);
        User existingUser = repository.save(testUser);

        Set<Long> newRoles = new HashSet<>();
        newRoles.add(roleRepository.findByLabel("ADMIN").orElseThrow().getId());
        newRoles.add(roleRepository.findByLabel("TEACHER").orElseThrow().getId());

        mockMvc.perform(put("/api/users/" + existingUser.getId() + "/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRoles)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("ADMIN", "TEACHER")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testSoftDeleteById_GivenValidId_ChangesRecordStatus() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByLabel("USER").orElseThrow());
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        User testUser = createTestUser("John", "Doe", genre, roles);
        User toDelete = repository.save(testUser);

        mockMvc.perform(delete("/api/users/" + toDelete.getId()))
                .andExpect(status().isNoContent());
        User softDeleted = repository.findById(toDelete.getId()).orElseThrow();
        assertEquals(RecordStatus.TO_DELETE, softDeleted.getRecordStatus());
    }
}