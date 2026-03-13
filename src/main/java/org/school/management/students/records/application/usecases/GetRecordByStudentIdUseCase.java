package org.school.management.students.records.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.application.mapper.StudentRecordApplicationMapper;
import org.school.management.students.records.domain.exception.RecordNotFoundException;
import org.school.management.students.records.domain.repository.StudentRecordRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Obtiene el legajo completo del estudiante con todos sus documentos.
 * El legajo es único por estudiante — no se busca por año.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetRecordByStudentIdUseCase {

    private final StudentRecordRepository recordRepository;
    private final StudentRecordApplicationMapper mapper;

    public StudentRecordResponse execute(UUID studentId) {
        log.debug("Fetching record for studentId: {}", studentId);

        return recordRepository
                .findByStudentId(StudentPersonalDataId.from(studentId))
                .map(mapper::toRecordResponse)
                .orElseThrow(() -> RecordNotFoundException.byStudentId(studentId));
    }

    /**
     * Método package-private reutilizable por otros use cases
     * que ya tienen la entidad cargada (evita doble consulta).
     */
    StudentRecordResponse buildResponse(
            org.school.management.students.records.domain.model.StudentRecord record) {
        return mapper.toRecordResponse(record);
    }
}