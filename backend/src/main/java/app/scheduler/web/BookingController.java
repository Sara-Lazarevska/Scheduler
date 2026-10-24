package app.scheduler.web;

import java.util.List;

import app.scheduler.dto.BookSlotRequest;
import app.scheduler.dto.BookingResponse;
import app.scheduler.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/slots/{slotId}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(@PathVariable Long slotId, @Valid @RequestBody BookSlotRequest request) {
        return service.book(slotId, request);
    }

    @GetMapping("/bookings")
    public List<BookingResponse> list() {
        return service.listAll();
    }
}