package com.danielmichalski.bookingservice.property.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.danielmichalski.bookingservice.property.repository.PropertyBlocksRepository;
import com.danielmichalski.bookingservice.property.repository.PropertyBookingsRepository;
import com.danielmichalski.bookingservice.property.service.PropertiesService;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyBookingsValidatorTest {

  @Mock
  private DateValidator dateValidator;
  @Mock
  private PropertiesService propertiesService;
  @Mock
  private PropertyBookingsRepository propertyBookingsRepository;
  @Mock
  private PropertyBlocksRepository propertyBlocksRepository;
  @InjectMocks
  private PropertyBookingsValidator underTest;

  @Nested
  class ValidateBookingTests {

    private UUID propertyId;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

    @BeforeEach
    void setUp() {
      propertyId = UUID.randomUUID();
      startDate = OffsetDateTime.now().plusDays(3);
      endDate = OffsetDateTime.now().plusDays(7);
    }

    @Test
    void happyPath() {
      when(propertyBookingsRepository.bookingExistsWithinRange(propertyId, startDate, endDate)).thenReturn(false);
      when(propertyBlocksRepository.blockExistsWithinRange(propertyId, startDate, endDate)).thenReturn(false);

      underTest.validateBooking(propertyId, startDate, endDate);

      verify(dateValidator).validateStartDateBeforeEndDate(startDate, endDate);
      verify(propertiesService).validatePropertyExists(propertyId);
    }

    @Test
    void bookingAlreadyExistsWithinTheRange_shouldThrowAnException() {
      when(propertyBookingsRepository.bookingExistsWithinRange(propertyId, startDate, endDate)).thenReturn(true);
      when(propertyBlocksRepository.blockExistsWithinRange(propertyId, startDate, endDate)).thenReturn(false);

      assertThrows(
          IllegalArgumentException.class,
          () -> underTest.validateBooking(propertyId, startDate, endDate),
          "Bookings cannot overlap with other bookings or blocks"
      );
    }

    @Test
    void blockAlreadyExistsWithinTheRange_shouldThrowAnException() {
      when(propertyBookingsRepository.bookingExistsWithinRange(propertyId, startDate, endDate)).thenReturn(false);
      when(propertyBlocksRepository.blockExistsWithinRange(propertyId, startDate, endDate)).thenReturn(true);

      assertThrows(
          IllegalArgumentException.class,
          () -> underTest.validateBooking(propertyId, startDate, endDate),
          "Bookings cannot overlap with other bookings or blocks"
      );
    }
  }

}
