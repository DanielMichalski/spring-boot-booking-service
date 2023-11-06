package com.danielmichalski.bookingservice.property.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PropertyBlockDto(UUID id,
                               OffsetDateTime dateCreated,
                               OffsetDateTime startDate,
                               OffsetDateTime endDate,
                               UUID propertyId) {
}
