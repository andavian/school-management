package org.school.management.students.health.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.shared.person.domain.valueobject.FullName;
import org.school.management.students.health.application.dto.request.UpdateHealthRecordRequest;
import org.school.management.students.health.application.dto.response.HealthRecordResponse;
import org.school.management.students.health.application.mapper.StudentHealthRecordApplicationMapper;
import org.school.management.students.health.domain.exception.HealthRecordNotFoundException;
import org.school.management.students.health.domain.model.StudentHealthRecord;
import org.school.management.students.health.domain.repository.StudentHealthRecordRepository;
import org.school.management.students.health.domain.valueobject.BloodType;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateHealthRecordUseCase")
class UpdateHealthRecordUseCaseTest {

    @Mock private StudentHealthRecordRepository healthRecordRepository;
    @Mock private StudentHealthRecordApplicationMapper mapper;

    @InjectMocks private UpdateHealthRecordUseCase useCase;

    private static final UUID STUDENT_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — actualiza ficha médica")
    void execute_happyPath_updatesHealthRecord() {
        UpdateHealthRecordRequest request = new UpdateHealthRecordRequest(
                null, null, null, null, null, null, null, null, null, null);

        StudentHealthRecord existing = mock(StudentHealthRecord.class);
        when(existing.getHealthRecordId()).thenReturn(mock(org.school.management.students.health.domain.valueobject.HealthRecordId.class));
        when(existing.getStudentId()).thenReturn(mock(org.school.management.students.personal.domain.valueobject.StudentPersonalDataId.class));
        when(existing.getCreatedAt()).thenReturn(java.time.LocalDateTime.now());
        when(existing.getBloodType()).thenReturn(BloodType.O_POSITIVE);
        when(existing.getHealthInsurance()).thenReturn("Swiss Medical");
        when(existing.getHealthInsuranceNumber()).thenReturn("99999");
        when(existing.getAllergies()).thenReturn("");
        when(existing.getChronicConditions()).thenReturn("");
        when(existing.getMedications()).thenReturn("");
        when(existing.getMedicalObservations()).thenReturn("");
        when(existing.getEmergencyContactName()).thenReturn(FullName.of("Pedro", "Gómez"));
        when(existing.getEmergencyContactPhone()).thenReturn(null);

        when(healthRecordRepository.findByStudentId(any())).thenReturn(Optional.of(existing));
        when(healthRecordRepository.save(any(StudentHealthRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toHealthRecordResponse(any(StudentHealthRecord.class))).thenReturn(mock(HealthRecordResponse.class));

        HealthRecordResponse result = useCase.execute(STUDENT_ID, request);

        assertThat(result).isNotNull();
        verify(healthRecordRepository).save(any(StudentHealthRecord.class));
    }

    @Test
    @DisplayName("execute — no encontrada — lanza HealthRecordNotFoundException")
    void execute_notFound_throwsException() {
        UpdateHealthRecordRequest request = new UpdateHealthRecordRequest(
                "A+", null, null, null, null, null, null, null, null, null);

        when(healthRecordRepository.findByStudentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(STUDENT_ID, request))
                .isInstanceOf(HealthRecordNotFoundException.class);

        verify(healthRecordRepository, never()).save(any());
    }
}
