package com.danielmichalski.bookingservice.property.mother;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import com.danielmichalski.bookingservice.property.entity.PropertyBookingEntity;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertyBookingEntityMother {

  public static PropertyBookingEntity complete(UUID propertyId) {
    return new PropertyBookingEntity(
        UUID.randomUUID(),
        randomAlphanumeric(4),
        randomAlphanumeric(3),
        OffsetDateTime.now(),
        OffsetDateTime.now().plusDays(4),
        OffsetDateTime.now().plusDays(6),
        propertyId
    );
  }
}
