package org.school.management.students.health.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.person.domain.valueobject.FullName;
import org.school.management.shared.person.domain.valueobject.PhoneNumber;
import org.school.management.students.health.application.dto.request.UpdateHealthRecordRequest;
import org.school.management.students.health.application.dto.response.HealthRecordResponse;
import org.school.management.students.health.application.mapper.StudentHealthRecordApplicationMapper;
import org.school.management.students.health.domain.exception.HealthRecordNotFoundException;
import org.school.management.students.health.domain.model.StudentHealthRecord;
import org.school.management.students.health.domain.repository.StudentHealthRecordRepository;
import org.school.management.students.health.domain.valueobject.BloodType;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Actualiza la ficha médica de un estudiante (PATCH semántico).
 *
 * Solo los campos enviados en el request reemplazan los existentes.
 * Los campos null conservan el valor actual.
 * healthRecordId y studentId son inmutables — nunca se modifican.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateHealthRecordUseCase {

    private final StudentHealthRecordRepository healthRecordRepository;
    private final StudentHealthRecordApplicationMapper mapper;

    public HealthRecordResponse execute(UUID studentId, UpdateHealthRecordRequest request) {
        log.info("Updating health record for studentId: {}", studentId);

        var existing = healthRecordRepository
                .findByStudentId(StudentPersonalDataId.of(studentId))
                .orElseThrow(() -> HealthRecordNotFoundException.byStudentId(studentId));

        StudentHealthRecord updated = StudentHealthRecord.builder()
                // Campos de identidad — inmutables
                .healthRecordId(existing.getHealthRecordId())
                .studentId(existing.getStudentId())
                .createdAt(existing.getCreatedAt())
                // Campos actualizables — PATCH: usar nuevo valor si no es null, sino conservar
                .bloodType(request.bloodType() != null
                        ? BloodType.fromString(request.bloodType())
                        : existing.getBloodType())
                .healthInsurance(request.healthInsurance() != null
                        ? request.healthInsurance()
                        : existing.getHealthInsurance())
                .healthInsuranceNumber(request.healthInsuranceNumber() != null
                        ? request.healthInsuranceNumber()
                        : existing.getHealthInsuranceNumber())
                .allergies(request.allergies() != null
                        ? request.allergies()
                        : existing.getAllergies())
                .chronicConditions(request.chronicConditions() != null
                        ? request.chronicConditions()
                        : existing.getChronicConditions())
                .medications(request.medications() != null
                        ? request.medications()
                        : existing.getMedications())
                .medicalObservations(request.medicalObservations() != null
                        ? request.medicalObservations()
                        : existing.getMedicalObservations())
                .emergencyContactName(buildEmergencyName(request, existing))
                .emergencyContactPhone(request.emergencyContactPhone() != null
                        ? PhoneNumber.of(request.emergencyContactPhone())
                        : existing.getEmergencyContactPhone())
                .build();

        StudentHealthRecord saved = healthRecordRepository.save(updated);
        log.info("Health record updated successfully for studentId: {}", studentId);

        return mapper.toHealthRecordResponse(saved);
    }

    private FullName buildEmergencyName(UpdateHealthRecordRequest request, StudentHealthRecord existing) {
        String firstName = request.emergencyContactFirstName() != null
                ? request.emergencyContactFirstName()
                : existing.getEmergencyContactName().firstName();
        String lastName = request.emergencyContactLastName() != null
                ? request.emergencyContactLastName()
                : existing.getEmergencyContactName().lastName();
        return FullName.of(firstName, lastName);
    }
}