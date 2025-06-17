package com.gbroche.courseorganizer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.repository.GenreRepository;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class GenreControllerTest {
    @Autowired
    private GenreRepository repository;

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
    void testCreate_ShouldCreateAndReturnNewEntity() throws Exception {
        Genre toAdd = new Genre("Male");
        mockMvc.perform(post("/api/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Male"))
                .andExpect(jsonPath("$.recordStatus").value("SHOWN"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetById_GivenValidId_ReturnsCorrespondingEntity() throws Exception {
        Genre toRetrieve = repository.save(new Genre("Male"));
        mockMvc.perform(get("/api/genres/" + toRetrieve.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Male"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetById_GivenInvalidId_ReturnsNotFoundResponse() throws Exception {
        mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No corresponding entity found"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetAll() throws Exception {
        repository.save(new Genre("Male"));
        repository.save(new Genre("Female"));
        repository.save(new Genre("Non Binary"));
        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testUpdate_GivenValidInputs_ShouldReturnUpdatedRole() throws Exception {
        Genre toEdit = repository.save(new Genre("m"));
        toEdit.setLabel("Male");
        mockMvc.perform(put("/api/genres/" + toEdit.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toEdit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Male"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testSoftDeleteById_GivenValidId_ChangesRecordStatus() throws Exception {
        Genre toDelete = repository.save(new Genre("delete test"));
        mockMvc.perform(delete("/api/genres/" + toDelete.getId()))
                .andExpect(status().isNoContent());
        Genre softDeletedGenre = repository.findById(toDelete.getId()).orElseThrow();
        assertEquals(RecordStatus.TO_DELETE, softDeletedGenre.getRecordStatus());
    }
}
