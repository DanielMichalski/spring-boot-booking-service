package com.danielmichalski.bookingservice.property.mapper;

import com.danielmichalski.bookingservice.property.dto.PropertyBookingDto;
import com.danielmichalski.bookingservice.property.entity.PropertyBookingEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertyBookingMapper {

  public static PropertyBookingDto mapTaskDto(PropertyBookingEntity entity) {
    return new PropertyBookingDto(
        entity.id(),
        entity.guestFirstName(),
        entity.guestLastName(),
        entity.dateCreated(),
        entity.startDate(),
        entity.endDate(),
        entity.propertyId()
    );
  }

}
