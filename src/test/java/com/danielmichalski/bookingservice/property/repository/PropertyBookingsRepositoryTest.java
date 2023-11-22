package com.danielmichalski.bookingservice.property.repository;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.danielmichalski.bookingservice.property.entity.PropertyBookingEntity;
import com.danielmichalski.bookingservice.property.repository.helper.PropertiesTestDataHelper;
import com.danielmichalski.bookingservice.property.repository.helper.PropertyBookingsTestDataHelper;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class PropertyBookingsRepositoryTest {

  @Autowired
  private PropertiesTestDataHelper propertiesTestDataHelper;
  @Autowired
  private PropertyBookingsTestDataHelper propertyBookingsTestDataHelper;
  @Autowired
  private PropertyBookingsRepository underTest;

  @Nested
  class FindByIdTests {

    @Test
    void propertyBookingNotExists_shouldReturnEmptyOptional() {
      UUID notExistingPropertyId = UUID.randomUUID();

      Optional<PropertyBookingEntity> result = underTest.findById(notExistingPropertyId);

      assertThat(result).isNotPresent();
    }

    @Test
    void propertyBookingExists_shouldReturnOptionalWithAnEntity() {
      UUID propertyId = propertiesTestDataHelper.insertProperty(
          randomAlphanumeric(10),
          randomAlphanumeric(7)
      );
      OffsetDateTime startDate = OffsetDateTime.now()
          .plusDays(1)
          .truncatedTo(ChronoUnit.DAYS);
      OffsetDateTime endDate = OffsetDateTime.now()
          .plusDays(3)
          .truncatedTo(ChronoUnit.DAYS);

      PropertyBookingEntity entity = new PropertyBookingEntity(
          UUID.randomUUID(),
          randomAlphanumeric(4),
          randomAlphanumeric(3),
          OffsetDateTime.now(),
          startDate,
          endDate,
          propertyId
      );
      UUID returnedPropertyBookingId = propertyBookingsTestDataHelper.insertPropertyBooking(entity);

      Optional<PropertyBookingEntity> result = underTest.findById(returnedPropertyBookingId);

      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(entity);
    }
  }

  @Nested
  class BookingExistsWithinRangeTests {

    private UUID propertyId;
    private static final OffsetDateTime EXISTING_BOOKING_START_DATE = OffsetDateTime.now()
        .plusDays(5)
        .truncatedTo(ChronoUnit.DAYS);
    private static final OffsetDateTime EXISTING_BOOKING_END_DATE = OffsetDateTime.now()
        .plusDays(10)
        .truncatedTo(ChronoUnit.DAYS);

    @BeforeEach
    void setUp() {
      propertyId = propertiesTestDataHelper.insertProperty(
          randomAlphanumeric(10),
          randomAlphanumeric(7)
      );

      PropertyBookingEntity entity = new PropertyBookingEntity(
          UUID.randomUUID(),
          randomAlphanumeric(4),
          randomAlphanumeric(3),
          OffsetDateTime.now(),
          EXISTING_BOOKING_START_DATE,
          EXISTING_BOOKING_END_DATE,
          propertyId
      );
      propertyBookingsTestDataHelper.insertPropertyBooking(entity);
    }

    @ParameterizedTest(name = "[{index}] => [{0}], startDate={1}, endDate={1}")
    @MethodSource("provideRangesWithingExistingBookingRange")
    void bookingAlreadyExistsWithingTheRange_shouldReturnTrue(String testCaseDescription,
                                                              OffsetDateTime startDate,
                                                              OffsetDateTime endDate) {
      boolean result = underTest.bookingExistsWithinRange(propertyId, startDate, endDate);

      assertThat(result)
          .as(testCaseDescription)
          .isTrue();
    }

    @ParameterizedTest(name = "[{index}] => [{0}], startDate={1}, endDate={1}")
    @MethodSource("provideRangesNotWithingExistingBookingRange")
    void bookingNotExistsWithingTheRange_shouldReturnFalse(String testCaseDescription,
                                                           OffsetDateTime startDate,
                                                           OffsetDateTime endDate) {
      boolean result = underTest.bookingExistsWithinRange(propertyId, startDate, endDate);

      assertThat(result)
          .as(testCaseDescription)
          .isFalse();
    }

    private static Stream<Arguments> provideRangesWithingExistingBookingRange() {
      return Stream.of(
          Arguments.of(
              "startDate before existing startDate and endDate after existing endDate",
              EXISTING_BOOKING_START_DATE.minusDays(3),
              EXISTING_BOOKING_END_DATE.plusDays(4)
          ),
          Arguments.of(
              "startDate before existing startDate and endDate before existing endDate",
              EXISTING_BOOKING_START_DATE.minusDays(2),
              EXISTING_BOOKING_END_DATE.minusDays(1)
          ),
          Arguments.of(
              "startDate after existing startDate and endDate before existing endDate",
              EXISTING_BOOKING_START_DATE.plusDays(1),
              EXISTING_BOOKING_END_DATE.minusDays(2)
          ),
          Arguments.of(
              "startDate after existing startDate and endDate after existing endDate",
              EXISTING_BOOKING_START_DATE.plusDays(2),
              EXISTING_BOOKING_END_DATE.plusDays(4)
          )
      );
    }

    private static Stream<Arguments> provideRangesNotWithingExistingBookingRange() {
      return Stream.of(
          Arguments.of(
              "startDate before existing startDate and endDate before existing startDate",
              EXISTING_BOOKING_START_DATE.minusDays(3),
              EXISTING_BOOKING_START_DATE.minusDays(1)
          ),
          Arguments.of(
              "startDate after existing endDate and endDate after existing endDate",
              EXISTING_BOOKING_END_DATE.plusDays(1),
              EXISTING_BOOKING_END_DATE.plusDays(3)
          )
      );
    }
  }

  @Test
  void bookProperty_shouldInsertRecord() {
    UUID propertyBookingId = UUID.randomUUID();
    String guestFirstName = randomAlphanumeric(5);
    String guestLastName = randomAlphanumeric(7);
    OffsetDateTime dateCreated = OffsetDateTime.now();
    OffsetDateTime startDate = OffsetDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
    OffsetDateTime endDate = OffsetDateTime.now().plusDays(3).truncatedTo(ChronoUnit.DAYS);

    UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(7), randomAlphanumeric(10));
    PropertyBookingEntity propertyBookingEntity = new PropertyBookingEntity(
        propertyBookingId,
        guestFirstName,
        guestLastName,
        dateCreated,
        startDate,
        endDate,
        propertyId
    );

    underTest.bookProperty(propertyBookingEntity);

    PropertyBookingEntity storedPropertyBooking = propertyBookingsTestDataHelper.getPropertyBooking(propertyBookingId);
    assertThat(storedPropertyBooking).isEqualTo(propertyBookingEntity);
  }

  @Test
  void updateBooking_shouldUpdateRecord() {
    UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(7), randomAlphanumeric(10));
    PropertyBookingEntity bookingEntity = new PropertyBookingEntity(
        UUID.randomUUID(),
        randomAlphanumeric(5),
        randomAlphanumeric(7),
        OffsetDateTime.now(),
        OffsetDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS),
        OffsetDateTime.now().plusDays(3).truncatedTo(ChronoUnit.DAYS),
        propertyId
    );
    UUID propertyBookingId = propertyBookingsTestDataHelper.insertPropertyBooking(bookingEntity);

    PropertyBookingEntity bookingEntityToUpdate = new PropertyBookingEntity(
        bookingEntity.id(),
        randomAlphanumeric(3),
        randomAlphanumeric(2),
        bookingEntity.dateCreated(),
        OffsetDateTime.now().plusDays(6).truncatedTo(ChronoUnit.DAYS),
        OffsetDateTime.now().plusDays(10).truncatedTo(ChronoUnit.DAYS),
        bookingEntity.propertyId()
    );

    underTest.updateBooking(bookingEntityToUpdate);

    PropertyBookingEntity storedPropertyBooking = propertyBookingsTestDataHelper.getPropertyBooking(propertyBookingId);
    assertThat(storedPropertyBooking)
        .extracting(
            PropertyBookingEntity::id,
            PropertyBookingEntity::guestFirstName,
            PropertyBookingEntity::guestLastName,
            PropertyBookingEntity::dateCreated,
            PropertyBookingEntity::startDate,
            PropertyBookingEntity::endDate,
            PropertyBookingEntity::propertyId
        ).containsExactly(
            bookingEntity.id(),
            bookingEntityToUpdate.guestFirstName(),
            bookingEntityToUpdate.guestLastName(),
            bookingEntity.dateCreated(),
            bookingEntityToUpdate.startDate(),
            bookingEntityToUpdate.endDate(),
            bookingEntity.propertyId()
        );
  }

  @Test
  @Transactional
  void cancelBooking_shouldUpdateTheRecordBySettingDateDeleted() {
    UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(7), randomAlphanumeric(10));
    UUID propertyBookingId = UUID.randomUUID();
    PropertyBookingEntity propertyBooking = new PropertyBookingEntity(
        propertyBookingId,
        randomAlphanumeric(5),
        randomAlphanumeric(7),
        OffsetDateTime.now(),
        OffsetDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS),
        OffsetDateTime.now().plusDays(3).truncatedTo(ChronoUnit.DAYS),
        propertyId
    );
    propertyBookingsTestDataHelper.insertPropertyBooking(propertyBooking);
    propertyBooking = propertyBookingsTestDataHelper.getPropertyBooking(propertyBookingId);
    assertThat(propertyBooking).isNotNull()
        .extracting(PropertyBookingEntity::id)
        .isEqualTo(propertyBookingId);

    underTest.cancelBooking(propertyId, propertyBookingId);

    assertThrows(
        EmptyResultDataAccessException.class,
        () -> propertyBookingsTestDataHelper.getPropertyBooking(propertyBookingId)
    );
  }
}
