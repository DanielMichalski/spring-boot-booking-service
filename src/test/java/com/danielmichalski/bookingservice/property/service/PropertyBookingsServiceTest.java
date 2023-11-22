package com.danielmichalski.bookingservice.property.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.danielmichalski.bookingservice.common.date.CurrentDateTimeService;
import com.danielmichalski.bookingservice.common.exception.NotFoundException;
import com.danielmichalski.bookingservice.property.dto.BookPropertyRequest;
import com.danielmichalski.bookingservice.property.dto.PropertyBookingDto;
import com.danielmichalski.bookingservice.property.dto.UpdateBookingRequest;
import com.danielmichalski.bookingservice.property.entity.PropertyBookingEntity;
import com.danielmichalski.bookingservice.property.repository.PropertyBookingsRepository;
import com.danielmichalski.bookingservice.property.validator.PropertyBookingsValidator;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyBookingsServiceTest {

  @Mock
  private PropertyBookingsRepository propertyBookingsRepository;
  @Mock
  private CurrentDateTimeService currentDateTimeService;
  @Mock
  private PropertyBookingsValidator propertyBookingsValidator;
  @InjectMocks
  private PropertyBookingsService underTest;

  @Nested
  class BookPropertyTests {

    private UUID propertyId;
    private OffsetDateTime currentDateTime;
    private BookPropertyRequest bookPropertyRequest;

    @BeforeEach
    void setUp() {
      String guestFirstName = RandomStringUtils.randomAlphanumeric(10);
      String guestLastName = RandomStringUtils.randomAlphanumeric(20);
      OffsetDateTime startDate = OffsetDateTime.now().plusDays(1);
      OffsetDateTime endDate = OffsetDateTime.now().plusDays(3);

      propertyId = UUID.randomUUID();
      currentDateTime = OffsetDateTime.now();
      bookPropertyRequest = new BookPropertyRequest(
          guestFirstName,
          guestLastName,
          startDate,
          endDate
      );
    }

    @Test
    void happyPath() {
      when(currentDateTimeService.currentDateTime()).thenReturn(currentDateTime);

      PropertyBookingDto returnedDto = underTest.bookProperty(propertyId, bookPropertyRequest);

      ArgumentCaptor<PropertyBookingEntity> entityToStoreCaptor =
          ArgumentCaptor.forClass(PropertyBookingEntity.class);
      verify(propertyBookingsRepository).bookProperty(entityToStoreCaptor.capture());
      assertThat(entityToStoreCaptor.getValue())
          .extracting(
              (entity) -> Objects.nonNull(entity.id()),
              PropertyBookingEntity::guestFirstName,
              PropertyBookingEntity::guestLastName,
              PropertyBookingEntity::dateCreated,
              PropertyBookingEntity::startDate,
              PropertyBookingEntity::endDate,
              PropertyBookingEntity::propertyId
          ).containsExactly(
              true,
              bookPropertyRequest.guestFirstName(),
              bookPropertyRequest.guestLastName(),
              currentDateTime,
              bookPropertyRequest.startDate(),
              bookPropertyRequest.endDate(),
              propertyId
          );
      assertThat(returnedDto)
          .extracting(
              (dto) -> Objects.nonNull(dto.id()),
              PropertyBookingDto::guestFirstName,
              PropertyBookingDto::guestLastName,
              PropertyBookingDto::dateCreated,
              PropertyBookingDto::startDate,
              PropertyBookingDto::endDate,
              PropertyBookingDto::propertyId
          ).containsExactly(
              true,
              bookPropertyRequest.guestFirstName(),
              bookPropertyRequest.guestLastName(),
              currentDateTime,
              bookPropertyRequest.startDate(),
              bookPropertyRequest.endDate(),
              propertyId
          );
    }

    @Test
    void validatorTrowsAnException_shouldNotBookProperty() {
      IllegalArgumentException exception = new IllegalArgumentException("Book property exception");
      doThrow(exception)
          .when(propertyBookingsValidator)
          .validateBooking(propertyId, bookPropertyRequest.startDate(), bookPropertyRequest.endDate());

      assertThrows(
          exception.getClass(),
          () -> underTest.bookProperty(propertyId, bookPropertyRequest),
          exception.getMessage()
      );
      verifyNoInteractions(propertyBookingsRepository);
    }
  }

  @Nested
  class UpdateBookingTests {

    private UUID propertyId;
    private UUID bookingId;
    private UpdateBookingRequest updateBookingRequest;

    @BeforeEach
    void setUp() {
      String guestFirstName = RandomStringUtils.randomAlphanumeric(10);
      String guestLastName = RandomStringUtils.randomAlphanumeric(20);
      OffsetDateTime startDate = OffsetDateTime.now().plusDays(1);
      OffsetDateTime endDate = OffsetDateTime.now().plusDays(3);

      propertyId = UUID.randomUUID();
      bookingId = UUID.randomUUID();
      updateBookingRequest = new UpdateBookingRequest(
          guestFirstName,
          guestLastName,
          startDate,
          endDate
      );
    }

    @Test
    void happyPath() {
      UUID id = UUID.randomUUID();
      String guestFirstName = RandomStringUtils.randomAlphanumeric(10);
      String guestLastName = RandomStringUtils.randomAlphanumeric(20);
      OffsetDateTime dateCreated = OffsetDateTime.now();
      OffsetDateTime startDate = OffsetDateTime.now().minusDays(1);
      OffsetDateTime endDate = OffsetDateTime.now().minusDays(3);
      PropertyBookingEntity originalEntity = new PropertyBookingEntity(
          id,
          guestFirstName,
          guestLastName,
          dateCreated,
          startDate,
          endDate,
          propertyId
      );

      when(propertyBookingsRepository.findById(bookingId)).thenReturn(Optional.of(originalEntity));

      underTest.updateBooking(propertyId, bookingId, updateBookingRequest);

      ArgumentCaptor<PropertyBookingEntity> entityToStoreCaptor =
          ArgumentCaptor.forClass(PropertyBookingEntity.class);
      verify(propertyBookingsRepository).updateBooking(entityToStoreCaptor.capture());
      assertThat(entityToStoreCaptor.getValue())
          .extracting(
              PropertyBookingEntity::id,
              PropertyBookingEntity::guestFirstName,
              PropertyBookingEntity::guestLastName,
              PropertyBookingEntity::dateCreated,
              PropertyBookingEntity::startDate,
              PropertyBookingEntity::endDate,
              PropertyBookingEntity::propertyId
          ).containsExactly(
              id,
              updateBookingRequest.guestFirstName(),
              updateBookingRequest.guestLastName(),
              dateCreated,
              updateBookingRequest.startDate(),
              updateBookingRequest.endDate(),
              propertyId
          );
    }

    @Test
    void validatorTrowsAnException_shouldNotUpdateBooking() {
      IllegalArgumentException exception = new IllegalArgumentException("Bookings exception");
      doThrow(exception)
          .when(propertyBookingsValidator)
          .validateBooking(propertyId, updateBookingRequest.startDate(), updateBookingRequest.endDate());

      assertThrows(
          exception.getClass(),
          () -> underTest.updateBooking(propertyId, bookingId, updateBookingRequest),
          exception.getMessage()
      );
      verifyNoInteractions(propertyBookingsRepository);
    }

    @Test
    void propertyBookingEntityNotFound_shouldThrowNotFoundException_andNotUpdateBooking() {
      when(propertyBookingsRepository.findById(bookingId)).thenReturn(Optional.empty());

      assertThrows(
          NotFoundException.class,
          () -> underTest.updateBooking(propertyId, bookingId, updateBookingRequest),
          "Booking not found"
      );
      verifyNoMoreInteractions(propertyBookingsRepository);
    }
  }

  @Nested
  class CancelBookingTests {

    @Test
    void happyPath() {
      UUID propertyId = UUID.randomUUID();
      UUID bookingId = UUID.randomUUID();
      when(propertyBookingsRepository.cancelBooking(propertyId, bookingId)).thenReturn(false);

      assertThrows(
          NotFoundException.class,
          () -> underTest.cancelBooking(propertyId, bookingId),
          "Property booking not found2"
      );
    }

    @Test
    void bookingNotCancelled_shouldThrowAnException() {
      UUID propertyId = UUID.randomUUID();
      UUID bookingId = UUID.randomUUID();
      when(propertyBookingsRepository.cancelBooking(propertyId, bookingId)).thenReturn(true);

      assertThatNoException()
          .isThrownBy(
              () -> underTest.cancelBooking(propertyId, bookingId)
          );
    }
  }

}
