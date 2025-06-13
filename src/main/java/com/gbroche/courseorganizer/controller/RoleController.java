package com.gbroche.courseorganizer.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Role;
import com.gbroche.courseorganizer.repository.RoleRepository;

@RestController
@RequestMapping("api/roles")
public class RoleController {
    private final RoleRepository repository;

    public RoleController(RoleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Role> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Role foundRole = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(foundRole);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No corresponding entity found");
        }
    }

    @PostMapping
    public Role create(@RequestBody Role role) {
        return repository.saveAndFlush(role);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Role role) {
        try {
            Role existing = repository.findById(id).orElseThrow();
            existing.setLabel(role.getLabel());
            Role editedRole = repository.saveAndFlush(existing);
            return ResponseEntity.ok(editedRole);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("No corresponding entity found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update entity");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteById(@PathVariable Long id) {
        try {
            Role roleToSoftDelete = repository.findById(id).orElseThrow();
            roleToSoftDelete.setRecordStatus(RecordStatus.TO_DELETE);
            repository.saveAndFlush(roleToSoftDelete);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No corresponding entity found");
        }
    }
}
