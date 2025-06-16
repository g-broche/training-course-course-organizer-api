package com.gbroche.courseorganizer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbroche.courseorganizer.config.JwtProperties;
import com.gbroche.courseorganizer.dto.AuthRequest;
import com.gbroche.courseorganizer.dto.AuthResponse;
import com.gbroche.courseorganizer.dto.SignUpRequest;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.model.User;
import com.gbroche.courseorganizer.repository.GenreRepository;
import com.gbroche.courseorganizer.repository.RoleRepository;
import com.gbroche.courseorganizer.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @BeforeAll
    void setUpRelations() {
        roleRepository.save(new Role("ADMIN"));
        roleRepository.save(new Role("TEACHER"));
        roleRepository.save(new Role("USER"));

        genreRepository.save(new Genre("Female"));
        genreRepository.save(new Genre("Male"));
        genreRepository.save(new Genre("Non binary"));
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @AfterAll
    void clearRelations() {
        repository.deleteAll();
        roleRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    void testRegisterUser_GivenValidInput_ShouldCreateUserAndReturnValidToken() throws Exception {
        SignUpRequest toAdd = new SignUpRequest(
                "John",
                "Doe",
                "John.doe@test.Test",
                "testuser",
                (long) 2);

        String jsonPayload = objectMapper.writeValueAsString(toAdd);
        System.out.println("Request JSON: " + jsonPayload);
        MvcResult result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toAdd)))
                .andExpect(status().isOk())
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
        assertEquals("John.doe@test.Test", claims.getSubject(), "Subject (email) should match");
        assertNotNull(claims.getIssuedAt(), "IssuedAt should be set");
        assertNotNull(claims.getExpiration(), "Expiration should be set");
        assertTrue(claims.getExpiration().after(new Date()), "Token should not be expired");
    }

    @Test
    void testLogin_GivenValidInfo_ReturnsOkWithToken() throws Exception {
        User existing = new User("John", "Doe", "John.doe@test.Test");
        String clearPassword = "testuser";
        String hashedPassword = passwordEncoder.encode(clearPassword);
        existing.setPassword(hashedPassword);
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByLabel("USER"));
        existing.setRoles(roles);
        Genre genre = genreRepository.findById(2L).orElseThrow();
        existing.setGenre(genre);
        repository.save(existing);

        AuthRequest credentials = new AuthRequest("John.doe@test.Test", clearPassword);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
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
        assertEquals("John.doe@test.Test", claims.getSubject(), "Subject (email) should match");
        assertNotNull(claims.getIssuedAt(), "IssuedAt should be set");
        assertNotNull(claims.getExpiration(), "Expiration should be set");
        assertTrue(claims.getExpiration().after(new Date()), "Token should not be expired");
    }
}