package org.school.management.academic.application.usecases.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ListSubjectsUseCase {

    private final SubjectRepository subjectRepository;
    private final AcademicApplicationMapper mapper;

    public List<SubjectResponse> execute(Integer yearLevel, String orientationId, Boolean activeOnly) {
        log.debug("Listing subjects (yearLevel: {}, orientationId: {}, activeOnly: {})",
                yearLevel, orientationId, activeOnly);

        List<Subject> subjects;

        if (yearLevel != null && orientationId != null) {
            OrientationId oId = new OrientationId(UUID.fromString(orientationId));
            subjects = subjectRepository.findByYearLevelAndOrientation(
                    YearLevel.of(yearLevel), oId
            );
        } else if (yearLevel != null) {
            subjects = subjectRepository.findByYearLevel(YearLevel.of(yearLevel));
        } else if (Boolean.TRUE.equals(activeOnly)) {
            subjects = subjectRepository.findActiveSubjects();
        } else {
            subjects = subjectRepository.findAll();
        }

        return subjects.stream()
                .map(mapper::toSubjectResponse)
                .collect(Collectors.toList());
    }
}
