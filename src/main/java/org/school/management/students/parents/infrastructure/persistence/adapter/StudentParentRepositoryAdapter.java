package org.school.management.students.parents.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.students.parents.domain.model.StudentParent;
import org.school.management.students.parents.domain.repository.StudentParentRepository;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.school.management.students.parents.domain.valueobject.StudentParentId;
import org.school.management.students.parents.infrastructure.persistence.mapper.ParentPersistenceMapper;
import org.school.management.students.parents.infrastructure.persistence.repository.StudentParentJpaRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudentParentRepositoryAdapter implements StudentParentRepository {

    private final StudentParentJpaRepository jpaRepository;
    private final ParentPersistenceMapper mapper;

    @Override
    public Optional<StudentParent> findByStudentParentId(StudentParentId studentParentId) {
        return jpaRepository.findById(studentParentId.value())
                .map(mapper::toStudentParentDomain);
    }

    @Override
    public Optional<StudentParent> findByStudentIdAndParentId(
            StudentPersonalDataId studentId,
            ParentId parentId) {
        return jpaRepository
                .findByStudentIdAndParentId(studentId.value(), parentId.value())
                .map(mapper::toStudentParentDomain);
    }

    @Override
    public List<StudentParent> findAllByStudentId(StudentPersonalDataId studentId) {
        return jpaRepository.findAllByStudentId(studentId.value())
                .stream()
                .map(mapper::toStudentParentDomain)
                .toList();
    }

    @Override
    public List<StudentParent> findAllByParentId(ParentId parentId) {
        return jpaRepository.findAllByParentId(parentId.value())
                .stream()
                .map(mapper::toStudentParentDomain)
                .toList();
    }

    @Override
    public boolean existsByStudentIdAndParentId(
            StudentPersonalDataId studentId,
            ParentId parentId) {
        return jpaRepository.existsByStudentIdAndParentId(
                studentId.value(), parentId.value()
        );
    }

    @Override
    public boolean existsPrimaryContactForStudent(StudentPersonalDataId studentId) {
        return jpaRepository.existsPrimaryContactForStudent(studentId.value());
    }

    @Override
    public StudentParent save(StudentParent studentParent) {
        return mapper.toStudentParentDomain(
                jpaRepository.save(mapper.toStudentParentEntity(studentParent))
        );
    }
}