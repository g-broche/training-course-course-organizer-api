package com.gbroche.courseorganizer.controller;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbroche.courseorganizer.dto.UserDTO;
import com.gbroche.courseorganizer.enums.RecordStatus;
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

    public UserController(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
            GenreRepository genreRepository) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return repository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            User found = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(new UserDTO(found));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("No corresponding user found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not retrieve user due to internal error");
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
            User editedUser = repository.saveAndFlush(user);
            return ResponseEntity.ok(new UserDTO(editedUser));
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
