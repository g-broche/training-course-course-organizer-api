package com.gbroche.courseorganizer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.dto.StudentRequest;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.model.Student;
import com.gbroche.courseorganizer.repository.StudentRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:studenttest-${random.uuid};DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest extends PersonBasedTester {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testStudent", roles = { "ADMIN" })
    void testGetById_ShouldReturnFoundUser() throws Exception {
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        Student toCreate1 = createTestStudent("John", "Doe", genre, LocalDate.of(1990, 3, 15));
        Student toCreate2 = createTestStudent("Mathews", "Doe", genre, LocalDate.of(1993, 2, 21));
        Student toFind = repository.save(toCreate1);
        repository.save(toCreate2);
        mockMvc.perform(get("/api/students/" + toFind.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser(username = "testStudent", roles = { "ADMIN" })
    void testGetAll_ShouldReturnAllStudents() throws Exception {
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        Student toCreate1 = createTestStudent("John", "Doe", genre, LocalDate.of(1990, 3, 15));
        Student toCreate2 = createTestStudent("Mathews", "Doe", genre, LocalDate.of(1993, 2, 21));
        repository.save(toCreate1);
        repository.save(toCreate2);
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "testStudent", roles = { "ADMIN" })
    void testEdit_ShouldEditStudentAndReturnDTO() throws Exception {
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        Student toCreate = createTestStudent("John", "Doe", genre, LocalDate.of(1990, 3, 15));
        Student created = repository.save(toCreate);

        StudentRequest editRequest = new StudentRequest(
                "Joh",
                "Dont",
                "joh.dont@test.test",
                LocalDate.of(2000, 1, 1),
                3L);
        mockMvc.perform(put("/api/students/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Joh"))
                .andExpect(jsonPath("$.lastName").value("Dont"))
                .andExpect(jsonPath("$.email").value("joh.dont@test.test"))
                .andExpect(jsonPath("$.birthdate").value("2000-01-01"))
                .andExpect(jsonPath("$.genre").value("Non binary"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testSoftDeleteById_GivenValidId_ChangesRecordStatus() throws Exception {
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        Student toCreate = createTestStudent("John", "Doe", genre, LocalDate.of(1990, 3, 15));
        Student toDelete = repository.save(toCreate);

        mockMvc.perform(delete("/api/students/" + toDelete.getId()))
                .andExpect(status().isNoContent());
        Student softDeleted = repository.findById(toDelete.getId()).orElseThrow();
        assertEquals(RecordStatus.TO_DELETE, softDeleted.getRecordStatus());
    }
}
