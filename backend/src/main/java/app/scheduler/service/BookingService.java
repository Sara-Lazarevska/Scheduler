package app.scheduler.service;

import app.scheduler.dto.BookSlotRequest;
import app.scheduler.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse book(Long slotId, BookSlotRequest req);
    List<BookingResponse> listAll();
}
