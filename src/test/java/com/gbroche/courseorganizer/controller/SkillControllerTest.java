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

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Skill;
import com.gbroche.courseorganizer.repository.SkillRepository;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class SkillControllerTest {

    @Autowired
    private SkillRepository repository;

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
        Skill toAdd = new Skill("Curious", false);
        mockMvc.perform(post("/api/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Curious"))
                .andExpect(jsonPath("$.hardSkill").value(false))
                .andExpect(jsonPath("$.recordStatus").value("SHOWN"));
    }

    @Test
    void testGetById_GivenValidId_ReturnsCorrespondingEntity() throws Exception {
        Skill toRetrieve = repository.save(new Skill("Curious", false, LocalDateTime.now()));
        mockMvc.perform(get("/api/skills/" + toRetrieve.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Curious"));
    }

    @Test
    void testGetById_GivenInvalidId_ReturnsNotFoundResponse() throws Exception {
        mockMvc.perform(get("/api/skills/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No corresponding entity found"));
    }

    @Test
    void testGetAll() throws Exception {
        repository.save(new Skill("Curious", false, LocalDateTime.now()));
        repository.save(new Skill("MVC", true, LocalDateTime.now()));
        repository.save(new Skill("Spring", true, LocalDateTime.now()));
        mockMvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetAllHardSkills_ShouldOnlyReturnHardSkills() throws Exception {
        repository.save(new Skill("Curious", false, LocalDateTime.now()));
        repository.save(new Skill("MVC", true, LocalDateTime.now()));
        repository.save(new Skill("Spring", true, LocalDateTime.now()));
        mockMvc.perform(get("/api/skills/hard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAllHardSkills_ShouldOnlyReturnSoftSkills() throws Exception {
        repository.save(new Skill("Curious", false, LocalDateTime.now()));
        repository.save(new Skill("MVC", true, LocalDateTime.now()));
        repository.save(new Skill("Honest", false, LocalDateTime.now()));
        repository.save(new Skill("Logic", false, LocalDateTime.now()));
        mockMvc.perform(get("/api/skills/soft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testUpdate_GivenValidInputs_ShouldReturnUpdatedRole() throws Exception {
        Skill toEdit = repository.save(new Skill("Curi", false, LocalDateTime.now()));
        toEdit.setLabel("Curious");
        mockMvc.perform(put("/api/skills/" + toEdit.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toEdit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Curious"));
    }

    @Test
    void testSoftDeleteById_GivenValidId_ChangesRecordStatus() throws Exception {
        Skill toDelete = repository.save(new Skill("delete test", false, LocalDateTime.now()));
        mockMvc.perform(delete("/api/skills/" + toDelete.getId()))
                .andExpect(status().isNoContent());
        Skill softDeleted = repository.findById(toDelete.getId()).orElseThrow();
        assertEquals(RecordStatus.TO_DELETE, softDeleted.getRecordStatus());
    }
}
