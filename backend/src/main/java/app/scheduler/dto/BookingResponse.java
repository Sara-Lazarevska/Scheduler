package app.scheduler.dto;

import app.scheduler.domain.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingResponse(
        Long id,
        Long slotId,
        LocalDate date,
        LocalTime startTime,
        int durationMinutes,
        String position,
        String candidateName,
        String candidateEmail,
        String notes,
        LocalDateTime createdAt) {

    public static BookingResponse from(Booking b) {
        var s = b.getSlot();
        return new BookingResponse(b.getId(), s.getId(), s.getDate(), s.getStartTime(),
                s.getDurationMinutes(), s.getPosition(), b.getCandidateName(),
                b.getCandidateEmail(), b.getNotes(), b.getCreatedAt());
    }
}