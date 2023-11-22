package com.danielmichalski.bookingservice.property.validator;

import static org.mockito.Mockito.verify;

import com.danielmichalski.bookingservice.property.service.PropertiesService;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyBlocksValidatorTest {

  @Mock
  private DateValidator dateValidator;
  @Mock
  private PropertiesService propertiesService;
  @InjectMocks
  private PropertyBlocksValidator underTest;

  @Test
  void validateBlock() {
    UUID propertyId = UUID.randomUUID();
    OffsetDateTime startDate = OffsetDateTime.now().plusDays(1);
    OffsetDateTime endDate = OffsetDateTime.now().plusDays(4);

    underTest.validateBlock(propertyId, startDate, endDate);

    verify(dateValidator).validateStartDateBeforeEndDate(startDate, endDate);
    verify(propertiesService).validatePropertyExists(propertyId);
  }
}
