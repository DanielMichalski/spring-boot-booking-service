package com.danielmichalski.bookingservice.property.validator;

import java.time.OffsetDateTime;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class DateValidator {

  public void validateStartDateBeforeEndDate(OffsetDateTime startDate, OffsetDateTime endDate) {
    if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
      throw new IllegalArgumentException("Start date and end date must be set");
    }

    if (!startDate.isBefore(endDate)) {
      throw new IllegalArgumentException("Start date should be before end date");
    }
  }

}
