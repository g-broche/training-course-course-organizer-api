package com.gbroche.courseorganizer.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbroche.courseorganizer.model.Status;
import com.gbroche.courseorganizer.repository.StatusRepository;

@RestController
@RequestMapping("api/status")
public class StatusController {
    private final StatusRepository repository;

    public StatusController(StatusRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Status> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Status foundStatus = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(foundStatus);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No corresponding entity found");
        }
    }

    @PostMapping
    public Status create(@RequestBody Status status) {
        return repository.save(status);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Status status) {
        try {
            Status existing = repository.findById(id).orElseThrow();
            existing.setLabel(status.getLabel());
            Status editedStatus = repository.save(existing);
            return ResponseEntity.ok(editedStatus);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("No corresponding entity found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update entity");
        }
    }
}
