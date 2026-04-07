package org.school.management.resources.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.teachers.domain.valueobject.TeacherId;

import java.time.LocalDateTime;
import java.time.DayOfWeek;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reservation {

    @EqualsAndHashCode.Include
    private final ReservationId reservationId;
    private final ResourceId resourceId;
    private final TeacherId teacherId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String purpose;
    private final LocalDateTime createdAt;
    private LocalDateTime cancelledAt;

    public static Reservation create(ResourceId resourceId, TeacherId teacherId,
                                     LocalDateTime start, LocalDateTime end, String purpose) {
        if (resourceId == null) throw new IllegalArgumentException("ResourceId is required");
        if (teacherId == null) throw new IllegalArgumentException("TeacherId is required");
        if (start == null || end == null) throw new IllegalArgumentException("Start and end times are required");
        if (start.isAfter(end)) throw new IllegalArgumentException("Start time must be before end time");
        if (start.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Cannot reserve in the past");
        if (!isWithinSchoolHours(start, end)) throw new IllegalArgumentException("Reservation must be within school hours (7:45-12:45 or 13:15-18:15) on weekdays");
        if (start.getDayOfWeek() == DayOfWeek.SATURDAY || start.getDayOfWeek() == DayOfWeek.SUNDAY)
            throw new IllegalArgumentException("Reservations are not allowed on weekends");

        return Reservation.builder()
                .reservationId(ReservationId.generate())
                .resourceId(resourceId)
                .teacherId(teacherId)
                .startTime(start)
                .endTime(end)
                .purpose(purpose)
                .createdAt(LocalDateTime.now())
                .cancelledAt(null)
                .build();
    }

    public void cancel() {
        if (cancelledAt != null) throw new IllegalStateException("Reservation already cancelled");
        this.cancelledAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return cancelledAt == null;
    }

    private static boolean isWithinSchoolHours(LocalDateTime start, LocalDateTime end) {
        int startHour = start.getHour();
        int startMinute = start.getMinute();
        int endHour = end.getHour();
        int endMinute = end.getMinute();

        boolean morning = (startHour > 7 || (startHour == 7 && startMinute >= 45)) && endHour < 12 ||
                (endHour == 12 && endMinute <= 45);
        boolean afternoon = (startHour >= 13 && startHour < 18) || (startHour == 18 && startMinute <= 15);
        if (morning && end.isBefore(start.toLocalDate().atTime(12, 45))) return true;
        if (afternoon && start.isAfter(start.toLocalDate().atTime(13, 15)) && end.isBefore(start.toLocalDate().atTime(18, 15))) return true;
        return false;
    }
}