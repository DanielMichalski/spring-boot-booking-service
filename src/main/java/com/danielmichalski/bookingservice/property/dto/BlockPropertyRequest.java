
package com.danielmichalski.bookingservice.property.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Future;

import java.time.OffsetDateTime;

public record BlockPropertyRequest(@Nonnull
                                   @Future(message = "Start date must be in the future") OffsetDateTime startDate,
                                   @Nonnull
                                   @Future(message = "End date must be in the future") OffsetDateTime endDate) {

}
