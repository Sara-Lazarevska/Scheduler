package app.scheduler.dto;

import jakarta.validation.constraints.*;

public record BookSlotRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 255) String email,
        @Size(max = 1000) String notes) {
}