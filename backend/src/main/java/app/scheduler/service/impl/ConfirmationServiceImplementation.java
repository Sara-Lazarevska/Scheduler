package app.scheduler.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.scheduler.common.ApiException;
import app.scheduler.domain.Booking;
import app.scheduler.domain.InterviewSlot;
import app.scheduler.dto.ConfirmationResponse;
import app.scheduler.repository.BookingRepository;
import app.scheduler.service.ConfirmationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



@Service
public class ConfirmationServiceImplementation implements ConfirmationService {

    private static final Logger log = LoggerFactory.getLogger(ConfirmationServiceImplementation.class);
    private static final DateTimeFormatter DAY =
            DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH);

    private final BookingRepository bookings;
    private final RestClient http;
    private final String apiKey;
    private final String model;

    public ConfirmationServiceImplementation(BookingRepository bookings,
                                             @Value("${gemini.api-key:}") String apiKey,
                                             @Value("${gemini.model}") String model) {
        this.bookings = bookings;
        this.apiKey = apiKey;
        this.model = model;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(10000);
        this.http = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .requestFactory(factory)
                .build();
    }

    @Override
    public ConfirmationResponse generate(Long bookingId) {
        Booking booking = bookings.findByIdWithSlot(bookingId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found."));

        if (apiKey.isBlank()) {
            return new ConfirmationResponse(fallback(booking), false);
        }
        try {
            String text = callGemini(buildPrompt(booking));
            if (text != null && !text.isBlank()) {
                return new ConfirmationResponse(text.trim(), true);
            }
        } catch (Exception ex) {
            log.warn("Gemini call failed, using fallback message: {}", ex.getMessage());
        }
        return new ConfirmationResponse(fallback(booking), false);
    }

    private String callGemini(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("temperature", 0.7, "maxOutputTokens", 600));

        GeminiResponse response = http.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(GeminiResponse.class);

        return response == null ? null : response.firstText();
    }

    private String buildPrompt(Booking b) {
        InterviewSlot s = b.getSlot();
        return """
                Write a short, friendly confirmation message (2 to 3 sentences) for a job candidate \
                who just booked an interview. Address them by first name, mention the date and time, \
                and end with one practical preparation tip. Plain text only: no markdown, no emojis, \
                no placeholders, no subject line.

                Candidate first name: %s
                Position: %s
                Date: %s
                Time: %s
                Duration: %d minutes
                """.formatted(
                firstName(b.getCandidateName()),
                s.getPosition() == null ? "general interview" : s.getPosition(),
                s.getDate().format(DAY),
                s.getStartTime(),
                s.getDurationMinutes());
    }

    private String fallback(Booking b) {
        InterviewSlot s = b.getSlot();
        return "Hi %s, your interview is confirmed for %s at %s (%d minutes). Looking forward to meeting you!"
                .formatted(firstName(b.getCandidateName()), s.getDate().format(DAY),
                        s.getStartTime(), s.getDurationMinutes());
    }

    private static String firstName(String full) {
        return full.trim().split("\\s+")[0];
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GeminiResponse(List<Candidate> candidates) {
        String firstText() {
            if (candidates == null || candidates.isEmpty() || candidates.get(0).content() == null) return null;
            var parts = candidates.get(0).content().parts();
            return (parts == null || parts.isEmpty()) ? null : parts.get(0).text();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Candidate(Content content) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Content(List<Part> parts) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Part(String text) {}
    }
}