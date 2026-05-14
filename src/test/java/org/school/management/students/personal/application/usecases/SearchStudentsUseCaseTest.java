package org.school.management.students.personal.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.personal.application.dto.response.StudentSummaryResponse;
import org.school.management.students.personal.application.mapper.StudentPersonalDataApplicationMapper;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("SearchStudentsUseCase")
class SearchStudentsUseCaseTest {

    @Mock private StudentPersonalDataRepository studentRepository;
    @Mock private StudentPersonalDataApplicationMapper mapper;

    @InjectMocks private SearchStudentsUseCase useCase;

    @Test
    @DisplayName("execute — usa findByResidencePlaceId")
    void execute_byPlaceId_usesFindByPlace() {
        when(studentRepository.findByResidencePlaceId(any())).thenReturn(Collections.emptyList());

        List<StudentSummaryResponse> result = useCase.execute(null, null, UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("execute — sin criterios → findAll")
    void execute_noCriteria_usesFindAll() {
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());

        List<StudentSummaryResponse> result = useCase.execute(null, null, null);

        assertThat(result).isEmpty();
    }
}
