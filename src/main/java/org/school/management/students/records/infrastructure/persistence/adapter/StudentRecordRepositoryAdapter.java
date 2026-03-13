package org.school.management.students.records.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.StudentRecordRepository;
import org.school.management.students.records.domain.valueobject.RecordId;
import org.school.management.students.records.domain.valueobject.RecordNumber;
import org.school.management.students.records.infrastructure.persistence.entity.RecordDocumentEntity;
import org.school.management.students.records.infrastructure.persistence.entity.StudentRecordEntity;
import org.school.management.students.records.infrastructure.persistence.mapper.StudentRecordPersistenceMapper;
import org.school.management.students.records.infrastructure.persistence.repository.RecordDocumentJpaRepository;
import org.school.management.students.records.infrastructure.persistence.repository.StudentRecordJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudentRecordRepositoryAdapter implements StudentRecordRepository {

    private final StudentRecordJpaRepository recordJpaRepository;
    private final RecordDocumentJpaRepository documentJpaRepository;
    private final StudentRecordPersistenceMapper mapper;

    @Override
    public Optional<StudentRecord> findByRecordId(RecordId recordId) {
        return recordJpaRepository.findById(recordId.value())
                .map(this::toDomainWithDocuments);
    }

    @Override
    public Optional<StudentRecord> findByStudentId(StudentPersonalDataId studentId) {
        return recordJpaRepository.findByStudentId(studentId.value())
                .map(this::toDomainWithDocuments);
    }

    @Override
    public Optional<StudentRecord> findByRecordNumber(RecordNumber recordNumber) {
        return recordJpaRepository.findByRecordNumber(recordNumber.value())
                .map(this::toDomainWithDocuments);
    }

    @Override
    public List<StudentRecord> findAllByAcademicYearId(AcademicYearId academicYearId) {
        return recordJpaRepository.findAllByAcademicYearId(academicYearId.value())
                .stream()
                .map(this::toDomainWithDocuments)
                .toList();
    }

    @Override
    public boolean existsByStudentId(StudentPersonalDataId studentId) {
        return recordJpaRepository.existsByStudentId(studentId.value());
    }

    @Override
    public StudentRecord save(StudentRecord record) {
        // 1. Guardar el legajo
        StudentRecordEntity recordEntity = mapper.toEntity(record);
        StudentRecordEntity savedRecord = recordJpaRepository.save(recordEntity);

        // 2. Sincronizar documentos — eliminar los que ya no están y guardar los actuales
        List<RecordDocumentEntity> currentDocuments =
                documentJpaRepository.findAllByRecordId(savedRecord.getRecordId());

        // Eliminar documentos que fueron removidos del agregado
        List<java.util.UUID> domainDocumentIds = record.getDocuments().stream()
                .map(doc -> doc.getDocumentId().value())
                .toList();

        currentDocuments.stream()
                .filter(entity -> !domainDocumentIds.contains(entity.getDocumentId()))
                .forEach(entity -> documentJpaRepository.deleteByDocumentIdAndRecordId(
                        entity.getDocumentId(), savedRecord.getRecordId()
                ));

        // Guardar documentos actuales
        List<RecordDocumentEntity> documentEntities =
                mapper.toDocumentEntityList(record.getDocuments());
        documentJpaRepository.saveAll(documentEntities);

        // 3. Recargar con documentos actualizados
        List<RecordDocumentEntity> updatedDocuments =
                documentJpaRepository.findAllByRecordId(savedRecord.getRecordId());

        return mapper.toDomain(savedRecord, updatedDocuments);
    }

    // ── Helper privado ────────────────────────────────────────────────────

    private StudentRecord toDomainWithDocuments(StudentRecordEntity entity) {
        List<RecordDocumentEntity> documents =
                documentJpaRepository.findAllByRecordId(entity.getRecordId());
        return mapper.toDomain(entity, documents);
    }
}