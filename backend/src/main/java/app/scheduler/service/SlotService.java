package app.scheduler.service;

import app.scheduler.domain.enums.SlotStatus;
import app.scheduler.dto.CreateSlotRequest;
import app.scheduler.dto.SlotResponse;

import java.util.List;

public interface SlotService {
    SlotResponse create(CreateSlotRequest req);
    List<SlotResponse> list(SlotStatus status);
    void delete(Long id);
}
