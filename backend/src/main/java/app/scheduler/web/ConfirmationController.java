package app.scheduler.web;


import app.scheduler.dto.ConfirmationResponse;
import app.scheduler.service.ConfirmationService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class ConfirmationController {

    private final ConfirmationService service;

    public ConfirmationController(ConfirmationService service) {
        this.service = service;
    }

    @PostMapping("/{id}/confirmation")
    public ConfirmationResponse confirmation(@PathVariable Long id) {
        return service.generate(id);
    }
}