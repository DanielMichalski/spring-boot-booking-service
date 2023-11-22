package com.danielmichalski.bookingservice.property.validator;

import com.danielmichalski.bookingservice.property.repository.PropertyBlocksRepository;
import com.danielmichalski.bookingservice.property.repository.PropertyBookingsRepository;
import com.danielmichalski.bookingservice.property.service.PropertiesService;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyBookingsValidator {

  private final DateValidator dateValidator;
  private final PropertiesService propertiesService;
  private final PropertyBookingsRepository propertyBookingsRepository;
  private final PropertyBlocksRepository propertyBlocksRepository;

  public void validateBooking(UUID propertyId, OffsetDateTime startDate, OffsetDateTime endDate) {
    dateValidator.validateStartDateBeforeEndDate(startDate, endDate);
    propertiesService.validatePropertyExists(propertyId);
    validatePropertyAvailability(propertyId, startDate, endDate);
  }

  private void validatePropertyAvailability(UUID propertyId, OffsetDateTime startDate, OffsetDateTime endDate) {
    boolean bookingExists = propertyBookingsRepository.bookingExistsWithinRange(propertyId, startDate, endDate);
    boolean blockExists = propertyBlocksRepository.blockExistsWithinRange(propertyId, startDate, endDate);

    if (bookingExists || blockExists) {
      throw new IllegalArgumentException("Bookings cannot overlap with other bookings or blocks");
    }
  }

}
