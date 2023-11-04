package com.danielmichalski.bookingservice.property.validator;

import com.danielmichalski.bookingservice.property.service.PropertiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PropertyBlocksValidator {

    private final DateValidator dateValidator;
    private final PropertiesService propertiesService;

    public void validateBlock(UUID propertyId, OffsetDateTime startDate, OffsetDateTime endDate) {
        dateValidator.validateStartDateBeforeEndDate(startDate, endDate);
        propertiesService.validatePropertyExists(propertyId);
    }
}
