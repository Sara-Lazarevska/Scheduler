package app.scheduler.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.*;

public record CreateSlotRequest(
        @NotNull @FutureOrPresent LocalDate date,
        @NotNull LocalTime startTime,
        @Min(15) @Max(240) int durationMinutes,
        @Size(max = 100) String position) {
}