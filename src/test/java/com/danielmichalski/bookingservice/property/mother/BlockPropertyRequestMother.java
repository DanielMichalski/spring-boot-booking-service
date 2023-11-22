package com.danielmichalski.bookingservice.property.mother;

import com.danielmichalski.bookingservice.property.dto.BlockPropertyRequest;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockPropertyRequestMother {

  public static BlockPropertyRequest complete() {
    return new BlockPropertyRequest(
        OffsetDateTime.now().plusDays(4),
        OffsetDateTime.now().plusDays(6)
    );
  }
}
