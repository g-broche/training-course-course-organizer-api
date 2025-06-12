package com.gbroche.courseorganizer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.repository.RoleRepository;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class RoleControllerTest {

    @Autowired
    private RoleRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testCreate_ShouldCreateAndReturnNewEntity() throws Exception {
        Role roleToAdd = new Role("ADMIN");
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("ADMIN"));
    }

    @Test
    void testGetById_GivenValidId_ReturnsCorrespondingEntity() throws Exception {
        Role r = repository.save(new Role("ADMIN"));
        mockMvc.perform(get("/api/roles/" + r.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("ADMIN"));
    }

    @Test
    void testGetById_GivenInvalidId_ReturnsNotFoundResponse() throws Exception {
        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No corresponding entity found"));
    }

    @Test
    void testGetAll() throws Exception {
        Role r1 = repository.save(new Role("ADMIN"));
        Role r2 = repository.save(new Role("TEACHER"));
        Role r3 = repository.save(new Role("STUDENT"));
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].label").value("ADMIN"))
                .andExpect(jsonPath("$[1].label").value("TEACHER"))
                .andExpect(jsonPath("$[2].label").value("STUDENT"));
    }

    @Test
    void testUpdate_GivenValidInputs_ShouldReturnUpdatedRole() throws Exception {
        Role roleToEdit = repository.save(new Role("Adm"));
        roleToEdit.setLabel("ADMIN");
        mockMvc.perform(put("/api/roles/" + roleToEdit.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleToEdit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("ADMIN"));
    }
}
