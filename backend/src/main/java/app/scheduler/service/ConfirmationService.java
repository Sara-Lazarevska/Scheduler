package app.scheduler.service;

import app.scheduler.dto.ConfirmationResponse;

public interface ConfirmationService {
    ConfirmationResponse generate(Long bookingId);
}
