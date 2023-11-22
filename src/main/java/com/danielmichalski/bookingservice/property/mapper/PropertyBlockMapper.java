package com.danielmichalski.bookingservice.property.mapper;

import com.danielmichalski.bookingservice.property.dto.PropertyBlockDto;
import com.danielmichalski.bookingservice.property.entity.PropertyBlockEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertyBlockMapper {

  public static PropertyBlockDto mapTaskDto(PropertyBlockEntity entity) {
    return new PropertyBlockDto(
        entity.id(),
        entity.dateCreated(),
        entity.startDate(),
        entity.endDate(),
        entity.propertyId()
    );
  }

}
