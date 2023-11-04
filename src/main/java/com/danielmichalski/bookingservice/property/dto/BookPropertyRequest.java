package com.danielmichalski.bookingservice.property.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record BookPropertyRequest(
        @NotBlank @Size(max = 30) String guestFirstName,
        @NotBlank @Size(max = 50) String questLastName,
        @Nonnull @Future(message = "Start date must be in the future") OffsetDateTime startDate,
        @Nonnull @Future(message = "End date must be in the future") OffsetDateTime endDate) {
}
