package com.gbroche.courseorganizer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Status;
import com.gbroche.courseorganizer.repository.StatusRepository;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class StatusControllerTest {

    @Autowired
    private StatusRepository repository;

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
        Status ToAdd = new Status("Ongoing");
        mockMvc.perform(post("/api/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Ongoing"))
                .andExpect(jsonPath("$.recordStatus").value("SHOWN"));
    }

    @Test
    void testGetById_GivenValidId_ReturnsCorrespondingEntity() throws Exception {
        Status s = repository.save(new Status("Ongoing"));
        mockMvc.perform(get("/api/status/" + s.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Ongoing"));
    }

    @Test
    void testGetById_GivenInvalidId_ReturnsNotFoundResponse() throws Exception {
        mockMvc.perform(get("/api/status/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No corresponding entity found"));
    }

    @Test
    void testGetAll() throws Exception {
        repository.save(new Status("Ongoing"));
        repository.save(new Status("Planned"));
        repository.save(new Status("Abandonned"));
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testUpdate_GivenValidInputs_ShouldReturnUpdatedRole() throws Exception {
        Status toEdit = repository.save(new Status("og"));
        toEdit.setLabel("Ongoing");
        mockMvc.perform(put("/api/status/" + toEdit.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toEdit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Ongoing"));
    }

    @Test
    void testSoftDeleteById_GivenValidId_ChangesRecordStatus() throws Exception {
        Status toDelete = repository.save(new Status("delete test"));
        mockMvc.perform(delete("/api/status/" + toDelete.getId()))
                .andExpect(status().isNoContent());
        Status softDeleted = repository.findById(toDelete.getId()).orElseThrow();
        assertEquals(RecordStatus.TO_DELETE, softDeleted.getRecordStatus());
    }
}
