package com.gbroche.courseorganizer.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbroche.courseorganizer.dto.StudentDTO;
import com.gbroche.courseorganizer.dto.StudentRequest;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.Genre;
import com.gbroche.courseorganizer.model.Student;
import com.gbroche.courseorganizer.repository.GenreRepository;
import com.gbroche.courseorganizer.repository.StudentRepository;

@RestController
@RequestMapping("api/students")
public class StudentController {
    private final StudentRepository repository;
    private final GenreRepository genreRepository;

    public StudentController(StudentRepository repository, GenreRepository genreRepository) {
        this.repository = repository;
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public List<StudentDTO> getAll() {
        return repository.findAll().stream()
                .map(StudentDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody StudentRequest studentRequest) {
        if (repository.existsByEmail(studentRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        try {
            Student newStudent = new Student();
            newStudent.setFirstName(studentRequest.getFirstName());
            newStudent.setLastName(studentRequest.getLastName());
            newStudent.setEmail(studentRequest.getEmail());
            newStudent.setBirthdate(studentRequest.getBirthdate());

            Genre genre = genreRepository.findById(studentRequest.getGenreId()).orElseThrow();
            newStudent.setGenre(genre);

            Student savedStudent = repository.saveAndFlush(newStudent);

            return ResponseEntity.ok(new StudentDTO(savedStudent));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Student Creation failed");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody StudentRequest studentRequest) {
        try {
            Student toEdit = repository.findById(id).orElseThrow();
            toEdit.setFirstName(studentRequest.getFirstName());
            toEdit.setLastName(studentRequest.getLastName());
            toEdit.setEmail(studentRequest.getEmail());
            toEdit.setBirthdate(studentRequest.getBirthdate());

            Genre genre = genreRepository.findById(studentRequest.getGenreId()).orElseThrow();
            toEdit.setGenre(genre);

            Student savedStudent = repository.saveAndFlush(toEdit);

            return ResponseEntity.ok(new StudentDTO(savedStudent));
        } catch (NoSuchElementException e) {
            return ResponseEntity.internalServerError().body("No student found for this ID");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Student edition failed");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Student found = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(new StudentDTO(found));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("No corresponding student found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not retrieve student due to internal error");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteById(@PathVariable Long id) {
        try {
            Student toSoftDelete = repository.findById(id).orElseThrow();
            toSoftDelete.setRecordStatus(RecordStatus.TO_DELETE);
            repository.saveAndFlush(toSoftDelete);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No corresponding student found");
        }
    }
}
