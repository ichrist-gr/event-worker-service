package gr.ichrist.model.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record EventDto(
        @NotBlank String eventId,
        String description,
        String eventType,
        String eventStatus,
        Instant eventTime,
        String priority,
        String source,
        @NotBlank String topic,
        @NotBlank String payload
) {
}
