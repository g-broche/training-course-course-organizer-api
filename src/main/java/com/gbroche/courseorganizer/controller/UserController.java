package com.gbroche.courseorganizer.controller;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbroche.courseorganizer.dto.user.CreateUserRequestDTO;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.model.User;
import com.gbroche.courseorganizer.repository.GenreRepository;
import com.gbroche.courseorganizer.repository.RoleRepository;
import com.gbroche.courseorganizer.repository.UserRepository;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final GenreRepository genreRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
            GenreRepository genreRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public List<User> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            User found = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(found);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("No corresponding user found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUserRequestDTO userToAddDTO) {
        try {
            String hashedPassword = passwordEncoder.encode(userToAddDTO.getRawPassword());
            Genre genre = genreRepository.findById(userToAddDTO.getGenreId()).orElseThrow();
            Role userRole = roleRepository.findByLabel("USER");
            Set<Role> defaultRoles = new HashSet<>();
            defaultRoles.add(userRole);
            User newUser = new User();
            newUser.setFirstName(userToAddDTO.getFirstName());
            newUser.setLastName(userToAddDTO.getLastName());
            newUser.setEmail(userToAddDTO.getEmail());
            newUser.setPassword(hashedPassword);
            newUser.setRoles(defaultRoles);
            newUser.setGenre(genre);
            User createdUser = repository.saveAndFlush(newUser);
            String responseMessage = "User " + createdUser.getFirstName() + " " + createdUser.getLastName()
                    + " was created.";
            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not update user roles");
        }
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<?> changeUserRoles(@PathVariable Long id, @RequestBody Set<Long> roleIds) {
        try {
            User user;
            try {
                user = repository.findById(id).orElseThrow();
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(404).body("No corresponding user found");
            }
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            if (roles.size() != roleIds.size()) {
                return ResponseEntity.badRequest().body("One or more roles were not found");
            }
            user.setRoles(roles);
            User savedUser = repository.saveAndFlush(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not update user roles");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteById(@PathVariable Long id) {
        try {
            User toSoftDelete = repository.findById(id).orElseThrow();
            toSoftDelete.setRecordStatus(RecordStatus.TO_DELETE);
            repository.saveAndFlush(toSoftDelete);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No corresponding user found");
        }
    }
}
