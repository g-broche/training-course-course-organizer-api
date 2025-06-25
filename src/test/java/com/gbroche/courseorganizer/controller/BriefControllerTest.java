package com.gbroche.courseorganizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.dto.BriefRequest;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.*;
import com.gbroche.courseorganizer.repository.*;
import com.gbroche.courseorganizer.utils.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:promotest-${random.uuid};DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
class BriefControllerTest extends PersonBasedTester{

    @Autowired
    private BriefRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @PersistenceContext
    private EntityManager entityManager;

    private Map<String, Role> roles = new HashMap<>();
    private Map<String, Genre> genres = new HashMap<>();
    private Map<String, Status> status = new HashMap<>();

    private List<User> admins;
    private List<User> teachers;

    @BeforeAll
    @Override
    void setupTestData(){
        Status plannedStatus = statusRepository.save(new Status("Planned"));
        Status ongoingStatus = statusRepository.save(new Status("Ongoing"));
        Status endedStatus = statusRepository.save(new Status("Ended"));

        status.put("planned", plannedStatus);
        status.put("ongoing", ongoingStatus);
        status.put("ended", endedStatus);

        Role adminRole = roleRepository.save(new Role("ADMIN"));
        Role teacherRole = roleRepository.save(new Role("TEACHER"));
        Role userRole = roleRepository.save(new Role("USER"));

        roles.put("admin", adminRole);
        roles.put("teacher", teacherRole);
        roles.put("user", userRole);

        Genre genreFemale = genreRepository.save(new Genre("female"));
        Genre genreMale = genreRepository.save(new Genre("male"));
        Genre genreNonBinary = genreRepository.save(new Genre("non binary"));

        genres.put("female", genreFemale);
        genres.put("male", genreMale);
        genres.put("nonBinary", genreNonBinary);

        Set<Role> adminRoleSet = Set.of(adminRole, userRole);
        Set<Role> teacherRoleSet = Set.of(teacherRole, userRole);

        User admin1 = userRepository.save(createTestUser("Andre", "Ad", genreMale, adminRoleSet));
        User admin2 = userRepository.save(createTestUser("Belmond", "Ad", genreMale, adminRoleSet));
        admins = List.of(admin1, admin2);

        User teacher1 = userRepository.save(createTestUser("Alex", "teach", genreMale, adminRoleSet));
        User teacher2 = userRepository.save(createTestUser("Balmung", "teach", genreMale, adminRoleSet));
        teachers = List.of(teacher1, teacher2);
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetAll_ShouldReturnAllBriefs() throws Exception{
        User teacher = userRepository.findById(teachers.get(0).getId()).orElseThrow();
        Status briefStatus = statusRepository.findById(status.get("planned").getId()).orElseThrow();
        Brief brief1 = repository.save(new Brief(
               "test 1",
            "content test 1",
                briefStatus,
                teacher
        ));
        Brief brief2 = repository.save(new Brief(
               "test 2",
            "content test 2",
                briefStatus,
                teacher
        ));
        mockMvc.perform(get("/api/briefs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetById_GivenValidId_ShouldReturnAppropriateBrief() throws Exception{
        User teacher = userRepository.findById(teachers.get(0).getId()).orElseThrow();
        Status briefStatus = statusRepository.findById(status.get("planned").getId()).orElseThrow();
        Brief brief1 = repository.save(new Brief(
                "test 1",
                "content test 1",
                briefStatus,
                teacher
        ));
        Brief brief2 = repository.save(new Brief(
                "test 2",
                "content test 2",
                briefStatus,
                teacher
        ));
        mockMvc.perform(get("/api/briefs/"+brief2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test 2"));
    }

    @Test
    @Transactional
    void testCreate_GivenValidRequest_ShouldCreateAndReturnBrief() throws Exception {
        // Given: a real user in DB with encoded password
        User teacher = new User("John", "Doe", "john.doe@example.com");
        teacher.setGenre(genreRepository.findByLabel("male").orElseThrow());
        teacher.setPassword(passwordEncoder.encode("securePass123"));
        teacher.setRoles(Set.of(roleRepository.findByLabel("TEACHER").orElseThrow()));
        userRepository.saveAndFlush(teacher);
        UserDetails userDetails = userDetailsService.loadUserByUsername(teacher.getEmail());

        // Generate JWT
        String token = jwtUtil.generateToken(userDetails);
        Cookie jwtCookie = new Cookie("token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");

        Status briefStatus = statusRepository.findByLabel("Planned").orElseThrow();

        BriefRequest toAdd = new BriefRequest(
                "test create",
                "just a test",
                briefStatus.getId()
        );

        mockMvc.perform(post("/api/briefs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toAdd))
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test create"));
    }


    @Test
    @Transactional
    void testEdit_GivenValidRequest_EditsAndReturnsBrief() throws Exception{
        User teacher = userRepository.findById(teachers.get(0).getId()).orElseThrow();
        Status briefStatus = statusRepository.findById(status.get("planned").getId()).orElseThrow();
        Brief briefToEdit = repository.save(new Brief(
                "test",
                "content test",
                briefStatus,
                teacher
        ));
        teacher.setGenre(genreRepository.findByLabel("male").orElseThrow());
        userRepository.saveAndFlush(teacher);
        UserDetails userDetails = userDetailsService.loadUserByUsername(teacher.getEmail());
        // Generate JWT
        String token = jwtUtil.generateToken(userDetails);
        Cookie jwtCookie = new Cookie("token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        BriefRequest editRequest = new BriefRequest(
                "test edit",
                "just a test",
                briefStatus.getId()
        );

        mockMvc.perform(put("/api/briefs/"+briefToEdit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest))
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test edit"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testSoftDeleteById_GivenValidId_ChangesRecordStatus() throws Exception{
        User teacher = userRepository.findById(teachers.get(0).getId()).orElseThrow();
        Status briefStatus = statusRepository.findById(status.get("planned").getId()).orElseThrow();
        Brief toDelete = repository.save(new Brief(
                "test delete",
                "delete test",
                briefStatus,
                teacher
        ));
        mockMvc.perform(delete("/api/briefs/" + toDelete.getId()))
                .andExpect(status().isNoContent());
        Brief softDeletedGenre = repository.findById(toDelete.getId()).orElseThrow();
        assertEquals(RecordStatus.TO_DELETE, softDeletedGenre.getRecordStatus());
    }
}