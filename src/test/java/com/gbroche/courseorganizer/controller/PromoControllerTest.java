package com.gbroche.courseorganizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.dto.PromoRequest;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.*;
import com.gbroche.courseorganizer.repository.PromoRepository;
import com.gbroche.courseorganizer.repository.StatusRepository;
import com.gbroche.courseorganizer.repository.StudentRepository;
import com.gbroche.courseorganizer.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.*;

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
class PromoControllerTest extends PersonBasedTester{

    @Autowired
    private PromoRepository repository;

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

    @PersistenceContext
    private EntityManager entityManager;

    private Map<String, Role> roles = new HashMap<>();
    private Map<String, Genre> genres = new HashMap<>();
    private Map<String, Status> status = new HashMap<>();

    private List<User> admins;
    private List<User> teachers;

    private Promo rawPromoData1;
    private Promo rawPromoData2;

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

        Genre genreFemale = genreRepository.save(new Genre("Female"));
        Genre genreMale = genreRepository.save(new Genre("Male"));
        Genre genreNonBinary = genreRepository.save(new Genre("Non binary"));

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

        rawPromoData1 = new Promo(
                "Course1",
                "test 1",
                LocalDate.of(2024, 9, 10),
                LocalDate.of(2025, 9, 24)
        );
        rawPromoData1.setStatus(status.get("ongoing"));
        rawPromoData2 = new Promo(
                "Course2",
                "test 2",
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2026, 1, 24)
        );
        rawPromoData2.setStatus(status.get("planned"));
    }

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetAll_ShouldReturnAllPromos() throws Exception{
        Promo created1 = repository.save(createPromoFromTemplate(rawPromoData1));
        Promo created2 = repository.save(createPromoFromTemplate(rawPromoData2));

        mockMvc.perform(get("/api/promos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testGetById_GivenValidId_ShouldReturnCorrespondingEntity() throws Exception{
        Promo created1 = repository.save(createPromoFromTemplate(rawPromoData1));
        Promo created2 = repository.save(createPromoFromTemplate(rawPromoData2));

        mockMvc.perform(get("/api/promos/"+created2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Course2"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testCreate_GivenValidData_ShouldCreateAndReturnEntity() throws Exception {
        PromoRequest creationRequest = new PromoRequest();
        creationRequest.setName("createTest");
        creationRequest.setDescription("creation test");
        creationRequest.setStartDate(LocalDate.of(2025, 10, 15));
        creationRequest.setEndDate(LocalDate.of(2026, 11, 13));
        creationRequest.setStatus(status.get("planned").getId());
        creationRequest.setTeamIds(Set.of(
                admins.get(0).getId(),
                teachers.get(0).getId()
        ));

        mockMvc.perform(post("/api/promos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("createTest"))
                .andExpect(jsonPath("$.status").value("Planned"))
                .andExpect(jsonPath("$.team.length()").value(2));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testEdit_GivenValidValues_UpdatesAndReturnPromo() throws Exception{
        Promo toEdit = repository.save(createPromoFromTemplate(rawPromoData2));

        PromoRequest editionRequest = new PromoRequest();
        editionRequest.setName("editTest");
        editionRequest.setDescription("Edition test");
        editionRequest.setStartDate(LocalDate.of(2025, 10, 15));
        editionRequest.setEndDate(LocalDate.of(2026, 11, 13));
        editionRequest.setStatus(status.get("planned").getId());
        editionRequest.setTeamIds(Set.of(
                admins.get(0).getId(),
                teachers.get(0).getId()
        ));

        mockMvc.perform(post("/api/promos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("editTest"))
                .andExpect(jsonPath("$.status").value("Planned"))
                .andExpect(jsonPath("$.team.length()").value(2));
    }

    @Test
    @Transactional
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void getPromoStudents_GivenValidPromoId_ReturnsAssociatedStudents() throws Exception{
        Promo promo = createPromoFromTemplate(rawPromoData1);
        Genre genreMale =  genreRepository.findByLabel("Male").orElseThrow();
        Student student1 = studentRepository.save(createTestStudent("Allen", "study", genreMale, null));
        Student student2 = studentRepository.save(createTestStudent("Bert", "study", genreMale, null));
        promo.addStudent(student1);
        promo.addStudent(student2);
        repository.save(promo);

        mockMvc.perform(get("/api/promos/"+promo.getId()+"/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Transactional
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void addStudentToPromo_GivenValidStudent_AddStudentAndReturnsUpdatedPromo() throws Exception{
        Promo promo = createPromoFromTemplate(rawPromoData1);
        Genre genreMale =  genreRepository.findByLabel("Male").orElseThrow();
        Student student = studentRepository.save(createTestStudent("Allen", "study", genreMale, null));
        repository.save(promo);

        mockMvc.perform(post("/api/promos/"+promo.getId()+"/students/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students.length()").value(1))
                .andExpect(jsonPath("$.students[0].firstName").value("Allen"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void removeStudentFromPromo_GivenValidStudent_RemovesStudentAndReturnsUpdatedPromo() throws Exception {
        Promo promo = createPromoFromTemplate(rawPromoData1);
        Genre genreMale =  genreRepository.findByLabel("Male").orElseThrow();
        Student student = studentRepository.save(createTestStudent("Allen", "study", genreMale, null));
        promo.addStudent(student);
        repository.save(promo);

        assertEquals(1, promo.getStudents().size(), "Student must be added for controller test");

        mockMvc.perform(post("/api/promos/"+promo.getId()+"/students/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students.length()").value(0));
    }

    @Test
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void testSoftDeleteById_GivenValidId_ChangesRecordStatus() throws Exception{
        Promo toDelete = repository.save(createPromoFromTemplate(rawPromoData1));
        mockMvc.perform(delete("/api/promos/" + toDelete.getId()))
                .andExpect(status().isNoContent());

        entityManager.clear();
        Promo softDeleted = repository.findById(toDelete.getId()).orElseThrow();
        assertEquals(RecordStatus.TO_DELETE, softDeleted.getRecordStatus());
    }

    private Promo createPromoFromTemplate(Promo template) {
        Promo clone = new Promo();
        clone.setName(template.getName());
        clone.setDescription(template.getDescription());
        clone.setStartDate(template.getStartDate());
        clone.setEndDate(template.getEndDate());
        clone.setStatus(template.getStatus());
        return clone;
    }
    private Student createStudentFromTemplate(Student student) {
        Student clone = new Student();
        clone.setFirstName(student.getFirstName());
        clone.setLastName(student.getLastName());
        clone.setEmail(student.getEmail());
        clone.setBirthdate(student.getBirthdate());
        clone.setGenre(student.getGenre());
        return clone;
    }
}