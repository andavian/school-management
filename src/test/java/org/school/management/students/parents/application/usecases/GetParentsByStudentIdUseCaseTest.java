package org.school.management.students.parents.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.parents.application.dto.response.StudentParentResponse;
import org.school.management.students.parents.application.mapper.ParentApplicationMapper;
import org.school.management.students.parents.domain.repository.ParentRepository;
import org.school.management.students.parents.domain.repository.StudentParentRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetParentsByStudentIdUseCase")
class GetParentsByStudentIdUseCaseTest {

    @Mock private StudentParentRepository studentParentRepository;
    @Mock private ParentRepository parentRepository;
    @Mock private ParentApplicationMapper mapper;

    @InjectMocks private GetParentsByStudentIdUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — lista padres del estudiante")
    void execute_happyPath_listsParents() {
        when(studentParentRepository.findAllByStudentId(any())).thenReturn(Collections.emptyList());

        List<StudentParentResponse> result = useCase.execute(UUID.randomUUID());

        assertThat(result).isEmpty();
    }
}
