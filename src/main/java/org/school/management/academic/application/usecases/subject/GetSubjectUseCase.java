package org.school.management.academic.application.usecases.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.SubjectNotFoundException;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetSubjectUseCase {

    private final SubjectRepository subjectRepository;
    private final AcademicApplicationMapper mapper;

    public SubjectResponse execute(String subjectId) {
        log.debug("Getting subject: {}", subjectId);

        Subject subject = subjectRepository
                .findById(new SubjectId(UUID.fromString(subjectId)))
                .orElseThrow(() -> new SubjectNotFoundException(
                        "Subject not found: " + subjectId
                ));

        return mapper.toSubjectResponse(subject);
    }
}
