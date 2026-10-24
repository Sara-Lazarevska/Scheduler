package app.scheduler.repository;

import app.scheduler.domain.InterviewSlot;
import app.scheduler.domain.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, Long> {
    List<InterviewSlot> findAllByOrderByDateAscStartTimeAsc();

    List<InterviewSlot> findByStatusOrderByDateAscStartTimeAsc(SlotStatus status);

    List<InterviewSlot> findByDate(LocalDate date);
}
