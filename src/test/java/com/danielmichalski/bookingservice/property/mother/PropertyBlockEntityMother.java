package com.danielmichalski.bookingservice.property.mother;

import com.danielmichalski.bookingservice.property.entity.PropertyBlockEntity;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertyBlockEntityMother {

  public static PropertyBlockEntity complete(UUID propertyId) {
    return new PropertyBlockEntity(
        UUID.randomUUID(),
        OffsetDateTime.now(),
        OffsetDateTime.now().plusDays(4),
        OffsetDateTime.now().plusDays(6),
        propertyId
    );
  }
}
