package com.danielmichalski.bookingservice.property.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PropertyBlockEntity(UUID id,
                                  OffsetDateTime dateCreated,
                                  OffsetDateTime startDate,
                                  OffsetDateTime endDate,
                                  UUID propertyId) {
}
