package app.scheduler.domain;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import app.scheduler.domain.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
@Table(name = "interview_slot")
public class InterviewSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Getter
    @Column(length = 100)
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Version
    private Long version;

    protected InterviewSlot() {
    }

    public InterviewSlot(LocalDate date, LocalTime startTime, int durationMinutes, String position) {
        this.date = date;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.position = position;
    }

    public LocalDateTime startsAt() {
        return LocalDateTime.of(date, startTime);
    }

    public LocalDateTime endsAt() {
        return startsAt().plusMinutes(durationMinutes);
    }

}