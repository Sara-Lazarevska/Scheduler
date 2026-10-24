package app.scheduler.service.impl;

import app.scheduler.common.ApiException;
import app.scheduler.domain.Booking;
import app.scheduler.domain.InterviewSlot;
import app.scheduler.domain.enums.SlotStatus;
import app.scheduler.dto.BookSlotRequest;
import app.scheduler.dto.BookingResponse;
import app.scheduler.repository.BookingRepository;
import app.scheduler.repository.InterviewSlotRepository;
import app.scheduler.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImplementation implements BookingService {

    private final InterviewSlotRepository slots;
    private final BookingRepository bookings;

    @Override
    @Transactional
    public BookingResponse book(Long slotId, BookSlotRequest req) {
        InterviewSlot slot = slots.findById(slotId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found."));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new ApiException(HttpStatus.CONFLICT, "This slot has already been booked.");
        }
        if (!slot.startsAt().isAfter(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This slot is in the past.");
        }

        slot.setStatus(SlotStatus.BOOKED);
        slots.saveAndFlush(slot); // @Version check: a concurrent booking fails here

        Booking booking = new Booking(slot, req.name().trim(), req.email().trim(), req.notes());
        return BookingResponse.from(bookings.saveAndFlush(booking)); // unique(slot_id) is the last safety net
    }


    @Override
    public List<BookingResponse> listAll() {
        return bookings.findAllWithSlot().stream().map(BookingResponse::from).toList();
    }
}
