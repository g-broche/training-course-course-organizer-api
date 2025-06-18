package com.gbroche.courseorganizer.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.config.JwtProperties;
import com.gbroche.courseorganizer.dto.AuthRequest;
import com.gbroche.courseorganizer.dto.AuthResponse;
import com.gbroche.courseorganizer.dto.SignUpRequest;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.model.User;
import com.gbroche.courseorganizer.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:usertest-${random.uuid};DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest extends PersonBasedTester {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testRegisterUser_GivenValidInput_ShouldCreateUserAndReturnValidToken() throws Exception {
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        SignUpRequest toAdd = new SignUpRequest(
                "John",
                "Doe",
                "john.doe@test.test",
                "testuser",
                genre.getId());

        MvcResult result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseContent, AuthResponse.class);

        String token = authResponse.getToken();
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3, "JWT should have 3 parts");

        String secret = jwtProperties.getSecret();
        byte[] keyBytes = secret.getBytes();

        // Parse and validate the token
        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
                .build()
                .parseClaimsJws(token);

        // Extract claims
        Claims claims = jwsClaims.getBody();

        // Assert claims
        assertEquals("john.doe@test.test", claims.getSubject(), "Subject (email) should match");
        assertNotNull(claims.getIssuedAt(), "IssuedAt should be set");
        assertNotNull(claims.getExpiration(), "Expiration should be set");
        assertTrue(claims.getExpiration().after(new Date()), "Token should not be expired");
    }

    @Test
    void testLogin_GivenValidInfo_ReturnsOkWithToken() throws Exception {
        User existing = new User("John", "Doe", "john.doe@test.test");
        String clearPassword = "testuser";
        String hashedPassword = passwordEncoder.encode(clearPassword);
        existing.setPassword(hashedPassword);
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByLabel("USER"));
        existing.setRoles(roles);
        Genre genre = genreRepository.findByLabel("Male").orElseThrow();
        existing.setGenre(genre);
        repository.save(existing);

        AuthRequest credentials = new AuthRequest("john.doe@test.test", clearPassword);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseContent, AuthResponse.class);

        String token = authResponse.getToken();
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3, "JWT should have 3 parts");

        String secret = jwtProperties.getSecret();
        byte[] keyBytes = secret.getBytes();

        // Parse and validate the token
        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
                .build()
                .parseClaimsJws(token);

        // Extract claims
        Claims claims = jwsClaims.getBody();

        // Assert claims
        assertEquals("john.doe@test.test", claims.getSubject(), "Subject (email) should match");
        assertNotNull(claims.getIssuedAt(), "IssuedAt should be set");
        assertNotNull(claims.getExpiration(), "Expiration should be set");
        assertTrue(claims.getExpiration().after(new Date()), "Token should not be expired");
    }
}