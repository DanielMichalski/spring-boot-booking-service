package com.danielmichalski.bookingservice.property.validator;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class DateValidator {

    public void validateStartDateBeforeEndDate(OffsetDateTime startDate, OffsetDateTime endDate) {
        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("Start date should be before end date");
        }
    }

}
