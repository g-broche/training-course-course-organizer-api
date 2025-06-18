package com.gbroche.courseorganizer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.repository.RoleRepository;

import jakarta.persistence.EntityManager;

@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:usertest-${random.uuid};DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class RoleControllerTest {

    @Autowired
    private RoleRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testCreate_ShouldCreateAndReturnNewEntity() throws Exception {
        Role roleToAdd = new Role("ADMIN");
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("ADMIN"))
                .andExpect(jsonPath("$.recordStatus").value("SHOWN"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetById_GivenValidId_ReturnsCorrespondingEntity() throws Exception {
        Role r = repository.save(new Role("ADMIN"));
        mockMvc.perform(get("/api/roles/" + r.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetById_GivenInvalidId_ReturnsNotFoundResponse() throws Exception {
        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No corresponding entity found"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetAll() throws Exception {
        repository.save(new Role("ADMIN"));
        repository.save(new Role("TEACHER"));
        repository.save(new Role("STUDENT"));
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testUpdate_GivenValidInputs_ShouldReturnUpdatedRole() throws Exception {
        Role roleToEdit = repository.save(new Role("Adm"));
        roleToEdit.setLabel("ADMIN");
        mockMvc.perform(put("/api/roles/" + roleToEdit.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleToEdit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testSoftDeleteById_GivenValidId_ChangesRecordStatus() throws Exception {
        Role toDelete = repository.save(new Role("delete test"));
        mockMvc.perform(delete("/api/roles/" + toDelete.getId()))
                .andExpect(status().isNoContent());

        entityManager.clear();
        Role softDeleted = repository.findById(toDelete.getId()).orElseThrow();
        assertEquals(RecordStatus.TO_DELETE, softDeleted.getRecordStatus());
    }
}
