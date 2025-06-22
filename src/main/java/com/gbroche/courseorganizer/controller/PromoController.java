package com.gbroche.courseorganizer.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.gbroche.courseorganizer.dto.PromoRequest;
import com.gbroche.courseorganizer.model.*;
import com.gbroche.courseorganizer.repository.*;
import com.gbroche.courseorganizer.service.PromoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbroche.courseorganizer.dto.PromoDTO;
import com.gbroche.courseorganizer.dto.StudentDTO;
import com.gbroche.courseorganizer.dto.StudentRequest;
import com.gbroche.courseorganizer.enums.RecordStatus;

@RestController
@RequestMapping("api/promos")
public class PromoController {
    private final PromoRepository repository;
    private final StudentRepository studentRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final PromoService promoService;

    public PromoController(
            PromoRepository repository,
            StatusRepository statusRepository,
            StudentRepository studentRepository,
            UserRepository userRepository,
            PromoService promoService) {
        this.repository = repository;
        this.statusRepository = statusRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.promoService = promoService;
    }

    @GetMapping
    public List<PromoDTO> getAll() {
        return repository.findAll().stream()
                .map(PromoDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Promo found = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(new PromoDTO(found));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("No corresponding promo found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not retrieve promo due to internal error");
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PromoRequest promoRequest) {
        try {
            boolean isStartBeforeEnd = promoRequest.getStartDate().isBefore(promoRequest.getEndDate());
            if(!isStartBeforeEnd){
                return ResponseEntity.badRequest().body("Date given for start is after date given for end");
            }
            Status promoStatus =
                    promoRequest.getStatusId() != null
                    ? statusRepository.findById(promoRequest.getStatusId()).orElseThrow()
                    : statusRepository.findByLabel("Planned").orElseThrow();
            Set<User> teamMembers =
                    !promoRequest.getTeamIds().isEmpty()
                    ? new HashSet<>(userRepository.findAllById(promoRequest.getTeamIds()))
                    : new HashSet<>();
            Promo newPromo = new Promo(
                    promoRequest.getName(),
                    promoRequest.getDescription(),
                    promoRequest.getStartDate(),
                    promoRequest.getEndDate()
            );
            if (!promoRequest.getTeamIds().isEmpty() && teamMembers.size() != promoRequest.getTeamIds().size()){
                throw new IllegalArgumentException("One or more invalid users were given to create the team");
            }
            newPromo.setStatus(promoStatus);
            newPromo.setUsers(teamMembers);

            Promo savedPromo = repository.saveAndFlush(newPromo);

            return ResponseEntity.ok(new PromoDTO(savedPromo));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Promo Creation failed");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody PromoRequest promoRequest) {
        try {
            Promo toEdit = repository.findById(id).orElseThrow();
            final boolean hasSameStatus = Objects.equals(toEdit.getStatus().getId(), promoRequest.getStatusId());
            if(!hasSameStatus){
                toEdit.setStatus(statusRepository.findById(promoRequest.getStatusId()).orElseThrow());
            }
            Set<Long> currentAssignedUserIds = toEdit.getUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            final boolean hasSameTeam = promoRequest.getTeamIds().equals(currentAssignedUserIds);
            if (!hasSameTeam){
                toEdit.setUsers(new HashSet<>(userRepository.findAllById(promoRequest.getTeamIds())));
            }
            toEdit.setName(promoRequest.getName());
            toEdit.setDescription(promoRequest.getDescription());
            toEdit.setStartDate(promoRequest.getStartDate());
            toEdit.setEndDate(promoRequest.getEndDate());

            Promo savedPromo = repository.saveAndFlush(toEdit);

            return ResponseEntity.ok(new PromoDTO(savedPromo));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("The request referenced an invalid status or user");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Promo edition failed");
        }
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<?> getPromoStudents(@PathVariable Long id) {
        try {
            Set<StudentDTO> promoStudents = promoService.getStudentsForPromo(id);
            System.out.println("Controller found "+promoStudents.size()+" students");
            return ResponseEntity.ok().body(promoStudents);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error occurred while attempting to get promo students");
        }
    }

    @PostMapping("/{id}/students/add")
    public ResponseEntity<?> addStudentToPromo(@PathVariable Long id, @RequestBody Long studentId) {
        try {
            Promo promo;
            try {
                promo = repository.findById(id).orElseThrow();
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(404).body("No corresponding promo found");
            }
            Student studentToAdd = studentRepository.findById(studentId).orElseThrow();
            promo.addStudent(studentToAdd);
            Promo savedPromo = repository.saveAndFlush(promo);
            return ResponseEntity.ok().body(new PromoDTO(savedPromo));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Invalid student given");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error occurred while attempting to add student");
        }
    }

    @PostMapping("/{id}/students/remove")
    public ResponseEntity<?> removeStudentFromPromo(@PathVariable Long id, @RequestBody Long studentId) {
        try {
            Promo promo;
            try {
                promo = repository.findById(id).orElseThrow();
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(404).body("No corresponding promo found");
            }
            Student studentToAdd = studentRepository.findById(studentId).orElseThrow();
            promo.getStudents().remove(studentToAdd);
            Promo savedPromo = repository.saveAndFlush(promo);
            return ResponseEntity.ok().body(new PromoDTO(savedPromo));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Invalid student given");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error occurred while attempting to add student");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteById(@PathVariable Long id) {
        try {
            Promo toSoftDelete = repository.findById(id).orElseThrow();
            toSoftDelete.setRecordStatus(RecordStatus.TO_DELETE);
            repository.saveAndFlush(toSoftDelete);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No corresponding promo found");
        }
    }
}
