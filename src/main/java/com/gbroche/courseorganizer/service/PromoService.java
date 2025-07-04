package com.gbroche.courseorganizer.service;

import com.gbroche.courseorganizer.dto.StudentDTO;
import com.gbroche.courseorganizer.model.Promo;
import com.gbroche.courseorganizer.model.Student;
import com.gbroche.courseorganizer.repository.PromoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromoService {

    private final PromoRepository promoRepository;

    public PromoService(PromoRepository promoRepository) {
        this.promoRepository = promoRepository;
    }

    @Transactional(readOnly = true)
    public Set<StudentDTO> getStudentsForPromo(Long promoId) {
        Promo promo = promoRepository.findWithStudentsById(promoId)
                .orElseThrow();

        // Access the lazy-loaded students while still in transaction/session
        return promo.getStudents().stream()
                .map(StudentDTO::new)
                .collect(Collectors.toSet());
    }
}
