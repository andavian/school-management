// org.school.management.attendance.infrastructure.persistence.adapter.DailyAttendanceRepositoryAdapter
package org.school.management.attendance.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.attendance.domain.model.DailyAttendance;
import org.school.management.attendance.domain.repository.DailyAttendanceRepository;
import org.school.management.attendance.domain.valueobject.DailyAttendanceId;
import org.school.management.attendance.infrastructure.persistence.mapper.DailyAttendancePersistenceMapper;
import org.school.management.attendance.infrastructure.persistence.repository.DailyAttendanceJpaRepository;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DailyAttendanceRepositoryAdapter implements DailyAttendanceRepository {

    private final DailyAttendanceJpaRepository jpaRepository;
    private final DailyAttendancePersistenceMapper mapper;

    @Override
    public Optional<DailyAttendance> findById(DailyAttendanceId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByStudentIdAndDate(StudentPersonalDataId studentId, LocalDate date) {
        return jpaRepository.existsByStudentIdAndAttendanceDate(studentId.value(), date);
    }

    @Override
    public List<DailyAttendance> findByGradeLevelIdAndDate(GradeLevelId gradeLevelId, LocalDate date) {
        return jpaRepository.findByGradeLevelIdAndAttendanceDate(gradeLevelId.value(), date)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<DailyAttendance> findByStudentIdAndAcademicYearId(StudentPersonalDataId studentId,
                                                                  AcademicYearId academicYearId) {
        return jpaRepository.findByStudentIdAndAcademicYearId(studentId.value(), academicYearId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public DailyAttendance save(DailyAttendance dailyAttendance) {
        var entity = mapper.toEntity(dailyAttendance);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}