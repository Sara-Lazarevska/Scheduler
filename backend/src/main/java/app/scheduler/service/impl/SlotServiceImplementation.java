package app.scheduler.service.impl;

import app.scheduler.common.ApiException;
import app.scheduler.domain.InterviewSlot;
import app.scheduler.domain.enums.SlotStatus;
import app.scheduler.dto.CreateSlotRequest;
import app.scheduler.dto.SlotResponse;
import app.scheduler.repository.InterviewSlotRepository;
import app.scheduler.service.SlotService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotServiceImplementation implements SlotService {

    private final InterviewSlotRepository slots;
    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
    @Override
    @Transactional
    public SlotResponse create(CreateSlotRequest req) {
        InterviewSlot slot = new InterviewSlot(req.date(), req.startTime(),
                req.durationMinutes(), blankToNull(req.position()));

        if (!slot.startsAt().isAfter(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "The slot must start in the future.");
        }

        boolean overlaps = slots.findByDate(req.date()).stream()
                .anyMatch(other -> slot.startsAt().isBefore(other.endsAt())
                        && other.startsAt().isBefore(slot.endsAt()));
        if (overlaps) {
            throw new ApiException(HttpStatus.CONFLICT, "This slot overlaps with an existing one.");
        }

        return SlotResponse.from(slots.save(slot));
    }

    @Override
    @Transactional()
    public List<SlotResponse> list(SlotStatus status) {
        List<InterviewSlot> result = (status == null)
                ? slots.findAllByOrderByDateAscStartTimeAsc()
                : slots.findByStatusOrderByDateAscStartTimeAsc(status);

        LocalDateTime now = LocalDateTime.now();
        return result.stream()
                .filter(s -> status != SlotStatus.AVAILABLE || s.startsAt().isAfter(now))
                .map(SlotResponse::from)
                .toList();
    }

    @Override
    public void delete(Long id) {
        InterviewSlot slot = slots.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found."));
        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new ApiException(HttpStatus.CONFLICT, "A booked slot cannot be deleted.");
        }
        slots.delete(slot);
    }
}
