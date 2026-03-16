package org.school.management.teachers.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.teachers.application.dto.response.TeacherSummaryResponse;
import org.school.management.teachers.application.mapper.TeacherApplicationMapper;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SearchTeachersUseCase {

    private final TeacherRepository teacherRepository;
    private final TeacherApplicationMapper mapper;

    public List<TeacherSummaryResponse> execute(String dni, String lastName) {
        log.debug("Searching teachers — dni={}, lastName={}", dni, lastName);

        if (dni != null && !dni.isBlank()) {
            return teacherRepository.findByDni(Dni.of(dni))
                    .map(mapper::toTeacherSummaryResponse)
                    .map(List::of)
                    .orElse(List.of());
        }

        if (lastName != null && !lastName.isBlank()) {
            return teacherRepository.findByLastName(lastName)
                    .stream()
                    .map(mapper::toTeacherSummaryResponse)
                    .toList();
        }

        // Sin filtros — retorna todos
        return teacherRepository.findAll()
                .stream()
                .map(mapper::toTeacherSummaryResponse)
                .toList();
    }
}