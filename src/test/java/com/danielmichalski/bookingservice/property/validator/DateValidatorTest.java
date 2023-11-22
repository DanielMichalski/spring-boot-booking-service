package com.danielmichalski.bookingservice.property.validator;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DateValidatorTest {

  private DateValidator dateValidator;

  @BeforeEach
  void setUp() {
    dateValidator = new DateValidator();
  }

  @Nested
  class ValidateStartDateBeforeEndDateTests {

    @ParameterizedTest
    @MethodSource("provideValidDates")
    void validDates_shouldNotThrowAnException(OffsetDateTime validFrom, OffsetDateTime validTo) {
      assertThatNoException()
          .isThrownBy(
              () -> dateValidator.validateStartDateBeforeEndDate(validFrom, validTo)
          );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDates")
    void invalidDates_shouldThrowAnException(OffsetDateTime validFrom, OffsetDateTime validTo, String expectedMessage) {
      assertThrows(
          IllegalArgumentException.class,
          () -> dateValidator.validateStartDateBeforeEndDate(validFrom, validTo),
          expectedMessage
      );
    }

    private static Stream<Arguments> provideValidDates() {
      OffsetDateTime currentDateTime = OffsetDateTime.now();
      return Stream.of(
          Arguments.of(currentDateTime.plusDays(3), currentDateTime.plusDays(4)),
          Arguments.of(currentDateTime.plusMonths(4), currentDateTime.plusMonths(8)),
          Arguments.of(currentDateTime.plusYears(1), currentDateTime.plusYears(2))
      );
    }

    private static Stream<Arguments> provideInvalidDates() {
      OffsetDateTime currentDateTime = OffsetDateTime.now();
      String nullDatesMessage = "Start date and end date must be set";
      String startDateIsNotBeforeEndDateMessage = "Start date and end date must be set";

      return Stream.of(
          Arguments.of(currentDateTime.plusDays(3), null, nullDatesMessage),
          Arguments.of(null, currentDateTime.plusDays(3), nullDatesMessage),
          Arguments.of(currentDateTime, currentDateTime, startDateIsNotBeforeEndDateMessage),
          Arguments.of(currentDateTime.plusDays(1), currentDateTime.plusDays(1), startDateIsNotBeforeEndDateMessage),
          Arguments.of(
              currentDateTime.plusMonths(2),
              currentDateTime.plusMonths(1),
              startDateIsNotBeforeEndDateMessage
          )
      );
    }
  }

}
