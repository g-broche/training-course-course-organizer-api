package com.gbroche.courseorganizer.controller;

import com.gbroche.courseorganizer.dto.*;
import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.model.*;
import com.gbroche.courseorganizer.repository.*;
import com.gbroche.courseorganizer.service.PromoService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/briefs")
public class BriefController {
    private final BriefRepository repository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;

    public BriefController(BriefRepository repository, StatusRepository statusRepository, UserRepository userRepository) {
        this.repository = repository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<BriefDTO> getAll() {
        return repository.findAll().stream()
                .map(BriefDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Brief found = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(new BriefDTO(found));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("No corresponding brief found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not retrieve brief due to internal error");
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BriefRequest briefRequest) {
        try {
            Status briefStatus =
                    briefRequest.getStatusId() != null
                    ? statusRepository.findById(briefRequest.getStatusId()).orElseThrow()
                    : statusRepository.findByLabel("Planned").orElseThrow();
            User author = userRepository.findById(briefRequest.getAuthorId()).orElseThrow();

            Brief newBrief = new Brief(
                    briefRequest.getName(),
                    briefRequest.getContent(),
                    briefStatus,
                    author
            );

            Brief savedBrief = repository.saveAndFlush(newBrief);

            return ResponseEntity.ok(new BriefDTO(savedBrief));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Brief Creation failed");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody BriefRequest briefRequest) {
        try {
            Brief toEdit = repository.findById(id).orElseThrow();
            final boolean isRequestFromAuthor = Objects.equals(toEdit.getAuthor().getId(), briefRequest.getAuthorId());
            if (!isRequestFromAuthor){
                return ResponseEntity.status(403).body("User who attempted to modify is not the author");
            }
            final boolean hasSameStatus = Objects.equals(toEdit.getStatus().getId(), briefRequest.getStatusId());
            if(!hasSameStatus){
                toEdit.setStatus(statusRepository.findById(briefRequest.getStatusId()).orElseThrow());
            }

            toEdit.setName(briefRequest.getName());
            toEdit.setContent(briefRequest.getContent());

            Brief savedPromo = repository.saveAndFlush(toEdit);

            return ResponseEntity.ok(new BriefDTO(savedPromo));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("The request referenced an invalid status or brief");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Brief edition failed");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteById(@PathVariable Long id) {
        try {
            Brief toSoftDelete = repository.findById(id).orElseThrow();
            toSoftDelete.setRecordStatus(RecordStatus.TO_DELETE);
            repository.saveAndFlush(toSoftDelete);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No corresponding promo found");
        }
    }
}
