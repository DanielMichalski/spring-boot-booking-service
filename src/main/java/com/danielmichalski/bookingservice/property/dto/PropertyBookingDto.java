package com.danielmichalski.bookingservice.property.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PropertyBookingDto(UUID id,
                                 String guestFirstName,
                                 String guestLastName,
                                 OffsetDateTime dateCreated,
                                 OffsetDateTime startDate,
                                 OffsetDateTime endDate,
                                 UUID propertyId) {
}
