//package org.school.management.course.infra.persistence.adapters;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.school.management.academic.domain.model.EvaluationPeriod;
//import org.school.management.academic.domain.repository.EvaluationPeriodRepository;
//import org.school.management.academic.infra.persistence.repository.EvaluationPeriodJpaRepository;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Repository
//@RequiredArgsConstructor
//@Slf4j
//@Transactional(readOnly = true)
//public class EvaluationPeriodRepositoryAdapter implements EvaluationPeriodRepository {
//
//    private final EvaluationPeriodJpaRepository jpaRepository;
//    private final AcademicPersistenceMapper mapper;
//
//    @Override
//    @Transactional
//    public EvaluationPeriod save(EvaluationPeriod period) {
//        log.debug("Saving evaluation period: {}", period.getName());
//        var entity = mapper.toEvaluationPeriodEntity(period);
//        var saved = jpaRepository.save(entity);
//        return mapper.toEvaluationPeriodDomain(saved);
//    }
//
//    @Override
//    public Optional<EvaluationPeriod> findById(PeriodId periodId) {
//        return jpaRepository.findById(periodId.getValue())
//                .map(mapper::toEvaluationPeriodDomain);
//    }
//
//    @Override
//    public List<EvaluationPeriod> findByAcademicYear(AcademicYearId academicYearId) {
//        return jpaRepository.findByAcademicYearIdOrderByPeriodNumber(academicYearId.getValue()).stream()
//                .map(mapper::toEvaluationPeriodDomain)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Optional<EvaluationPeriod> findByAcademicYearAndNumber(AcademicYearId academicYearId, int periodNumber) {
//        return jpaRepository.findByAcademicYearIdAndPeriodNumber(academicYearId.getValue(), periodNumber)
//                .map(mapper::toEvaluationPeriodDomain);
//    }
//
//    @Override
//    public Optional<EvaluationPeriod> findCurrentPeriod(AcademicYearId academicYearId) {
//        return jpaRepository.findCurrentPeriod(academicYearId.getValue())
//                .map(mapper::toEvaluationPeriodDomain);
//    }
//
//    @Override
//    public Optional<EvaluationPeriod> findByDate(AcademicYearId academicYearId, LocalDate date) {
//        return jpaRepository.findByDate(academicYearId.getValue(), date)
//                .map(mapper::toEvaluationPeriodDomain);
//    }
//
//    @Override
//    public List<EvaluationPeriod> findByStatus(PeriodStatus status) {
//        return jpaRepository.findByStatus(status.name()).stream()
//                .map(mapper::toEvaluationPeriodDomain)
//                .collect(Collectors.toList());
//    }
//}
