package app.scheduler.web;


import java.util.List;

import app.scheduler.domain.enums.SlotStatus;
import app.scheduler.dto.CreateSlotRequest;
import app.scheduler.dto.SlotResponse;
import app.scheduler.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlotResponse create(@Valid @RequestBody CreateSlotRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<SlotResponse> list(@RequestParam(required = false) SlotStatus status) {
        return service.list(status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}