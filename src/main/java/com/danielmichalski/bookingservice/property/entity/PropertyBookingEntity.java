package com.danielmichalski.bookingservice.property.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PropertyBookingEntity(UUID id,
                                    String guestFirstName,
                                    String guestLastName,
                                    OffsetDateTime dateCreated,
                                    OffsetDateTime startDate,
                                    OffsetDateTime endDate,
                                    UUID propertyId) {

}
