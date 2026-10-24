package app.scheduler.dto;

import app.scheduler.domain.InterviewSlot;
import app.scheduler.domain.enums.SlotStatus;

import java.time.LocalDate;
import java.time.LocalTime;



public record SlotResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        int durationMinutes,
        String position,
        SlotStatus status) {

    public static SlotResponse from(InterviewSlot s) {
        return new SlotResponse(s.getId(), s.getDate(), s.getStartTime(),
                s.getDurationMinutes(), s.getPosition(), s.getStatus());
    }
}