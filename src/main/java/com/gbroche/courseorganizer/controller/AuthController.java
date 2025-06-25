package com.gbroche.courseorganizer.controller;

import java.time.Duration;
import java.util.Set;

import com.gbroche.courseorganizer.dto.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbroche.courseorganizer.dto.AuthRequest;
import com.gbroche.courseorganizer.dto.SignUpRequest;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.model.User;
import com.gbroche.courseorganizer.repository.GenreRepository;
import com.gbroche.courseorganizer.repository.RoleRepository;
import com.gbroche.courseorganizer.repository.UserRepository;
import com.gbroche.courseorganizer.service.CustomUserDetailsService;
import com.gbroche.courseorganizer.utils.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenreRepository genreRepository;
    private final RoleRepository roleRepository;

    public AuthController(
            AuthenticationManager authManager,
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            GenreRepository genreRepository,
            RoleRepository roleRepository) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.genreRepository = genreRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest request, HttpServletResponse response) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        try {
            User newUser = new User();
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getRawPassword()));

            Genre genre = genreRepository.findById(request.getGenreId()).orElseThrow();
            newUser.setGenre(genre);

            Role userRole = roleRepository.findByLabel("USER").orElseThrow();
            newUser.setRoles(Set.of(userRole));

            User user = userRepository.saveAndFlush(newUser);

            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(),
                            request.getRawPassword()));

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false) // true in production
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .sameSite("Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            UserDTO userDTO = new UserDTO(user);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

            ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false) // true in production
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .sameSite("Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            UserDTO userDTO = new UserDTO(user);
            return ResponseEntity.ok(userDTO);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // only if using HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // <-- Expire immediately

        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}
