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

import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.repository.GenreRepository;

@RestController
@RequestMapping("api/genres")
public class GenreController {
    private final GenreRepository repository;

    public GenreController(GenreRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Genre> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Genre foundGenre = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(foundGenre);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No corresponding entity found");
        }
    }

    @PostMapping
    public Genre create(@RequestBody Genre genre) {
        return repository.save(genre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Genre genre) {
        try {
            Genre existing = repository.findById(id).orElseThrow();
            existing.setLabel(genre.getLabel());
            Genre edited = repository.save(existing);
            return ResponseEntity.ok(edited);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("No corresponding entity found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update entity");
        }
    }
}
