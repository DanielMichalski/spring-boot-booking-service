package com.danielmichalski.bookingservice.property.controller;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.danielmichalski.bookingservice.controller.ControllerTestBase;
import com.danielmichalski.bookingservice.property.dto.BookPropertyRequest;
import com.danielmichalski.bookingservice.property.dto.UpdateBookingRequest;
import com.danielmichalski.bookingservice.property.entity.PropertyBookingEntity;
import com.danielmichalski.bookingservice.property.mother.PropertyBookingEntityMother;
import com.danielmichalski.bookingservice.property.repository.helper.PropertiesTestDataHelper;
import com.danielmichalski.bookingservice.property.repository.helper.PropertyBookingsTestDataHelper;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
class PropertyBookingsControllerIT extends ControllerTestBase {

  private static final String CONTROLLER_URL = "/api/properties/%s/bookings";

  @Autowired
  private PropertiesTestDataHelper propertiesTestDataHelper;
  @Autowired
  private PropertyBookingsTestDataHelper propertyBookingsTestDataHelper;

  @Nested
  class BookPropertyTests {

    @Test
    void happyPath() throws Exception {
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));

      BookPropertyRequest request = new BookPropertyRequest(
          randomAlphanumeric(5),
          randomAlphanumeric(10),
          OffsetDateTime.now().plusDays(1),
          OffsetDateTime.now().plusDays(2)
      );

      String url = String.format(CONTROLLER_URL, propertyId);

      post(request, HttpStatus.CREATED, url)
          .andExpectAll(
              jsonPath("$.id", notNullValue()),
              jsonPath("$.guestFirstName", equalTo(request.guestFirstName())),
              jsonPath("$.guestLastName", equalTo(request.guestLastName())),
              jsonPath("$.dateCreated", notNullValue()),
              jsonPath("$.startDate", equalTo(formatDateToJsonDate(request.startDate()))),
              jsonPath("$.endDate", equalTo(formatDateToJsonDate(request.endDate()))),
              jsonPath("$.propertyId", equalTo(propertyId.toString()))
          );
    }

    @Test
    void propertyDoesNotExist_shouldNotBookProperty() throws Exception {
      UUID notExistingPropertyId = UUID.randomUUID();
      BookPropertyRequest request = new BookPropertyRequest(
          randomAlphanumeric(5),
          randomAlphanumeric(10),
          OffsetDateTime.now().plusDays(1),
          OffsetDateTime.now().plusDays(2)
      );

      String url = String.format(CONTROLLER_URL, notExistingPropertyId);

      post(request, HttpStatus.NOT_FOUND, url)
          .andExpect(
              jsonPath("$.message", equalTo("Property not found"))
          );
    }

    @Nested
    class ValidationTests {

      @ParameterizedTest(name = "[{index}] => [{0}] - {1}")
      @MethodSource("provideInvalidRequests")
      void checkBeanValidation(String fieldName,
                               String fieldValidationMessage,
                               BookPropertyRequest request) throws Exception {
        UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
        String url = String.format(CONTROLLER_URL, propertyId);

        post(request, HttpStatus.BAD_REQUEST, url)
            .andExpect(
                jsonPath(String.format("$.%s", fieldName), equalTo(fieldValidationMessage))
            );
      }

      private static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
            Arguments.of(
                "guestFirstName",
                "size must be between 0 and 30",
                new BookPropertyRequest(
                    randomAlphanumeric(31),
                    randomAlphanumeric(50),
                    OffsetDateTime.now().plusDays(3),
                    OffsetDateTime.now().plusDays(5)
                )
            ),
            Arguments.of(
                "guestLastName",
                "size must be between 0 and 50",
                new BookPropertyRequest(
                    randomAlphanumeric(30),
                    randomAlphanumeric(51),
                    OffsetDateTime.now().plusDays(3),
                    OffsetDateTime.now().plusDays(5)
                )
            ),
            Arguments.of(
                "startDate",
                "Start date must be in the future",
                new BookPropertyRequest(
                    randomAlphanumeric(30),
                    randomAlphanumeric(50),
                    OffsetDateTime.now().minusDays(1),
                    OffsetDateTime.now().plusDays(5)
                )
            ),
            Arguments.of(
                "endDate",
                "End date must be in the future",
                new BookPropertyRequest(
                    randomAlphanumeric(31),
                    randomAlphanumeric(50),
                    OffsetDateTime.now().plusDays(3),
                    OffsetDateTime.now().minusDays(1)
                )
            )
        );
      }
    }
  }

  @Nested
  class UpdateBookingTests {

    @Test
    void happyPath() throws Exception {
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
      PropertyBookingEntity propertyBookingEntity = PropertyBookingEntityMother.complete(propertyId);
      propertyBookingsTestDataHelper.insertPropertyBooking(propertyBookingEntity);

      UpdateBookingRequest request = new UpdateBookingRequest(
          randomAlphanumeric(3),
          randomAlphanumeric(7),
          OffsetDateTime.now().plusDays(10),
          OffsetDateTime.now().plusDays(14)
      );

      String url = String.format(CONTROLLER_URL, propertyId) + "/" + propertyBookingEntity.id();
      put(request, HttpStatus.NO_CONTENT, url)
          .andReturn();
    }

    @Test
    void propertyDoesNotExist_shouldNotUpdateProperty() throws Exception {
      UUID notExistingPropertyId = UUID.randomUUID();
      UUID bookingId = UUID.randomUUID();
      UpdateBookingRequest request = new UpdateBookingRequest(
          randomAlphanumeric(5),
          randomAlphanumeric(10),
          OffsetDateTime.now().plusDays(1),
          OffsetDateTime.now().plusDays(2)
      );

      String url = String.format(CONTROLLER_URL, notExistingPropertyId) + "/" + bookingId;

      put(request, HttpStatus.NOT_FOUND, url)
          .andExpectAll(
              jsonPath("$.message", equalTo("Property not found"))
          );
    }

    @Nested
    class ValidationTests {

      @ParameterizedTest(name = "[{index}] => [{0}] - {1}")
      @MethodSource("provideInvalidRequests")
      void checkBeanValidation(String fieldName,
                               String fieldValidationMessage,
                               BookPropertyRequest request) throws Exception {
        UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
        UUID bookingId = UUID.randomUUID();
        String url = String.format(CONTROLLER_URL, propertyId) + "/" + bookingId;

        put(request, HttpStatus.BAD_REQUEST, url)
            .andExpect(
                jsonPath(String.format("$.%s", fieldName), equalTo(fieldValidationMessage))
            );
      }

      private static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
            Arguments.of(
                "guestFirstName",
                "size must be between 0 and 30",
                new BookPropertyRequest(
                    randomAlphanumeric(31),
                    randomAlphanumeric(50),
                    OffsetDateTime.now().plusDays(3),
                    OffsetDateTime.now().plusDays(5)
                )
            ),
            Arguments.of(
                "guestLastName",
                "size must be between 0 and 50",
                new BookPropertyRequest(
                    randomAlphanumeric(30),
                    randomAlphanumeric(51),
                    OffsetDateTime.now().plusDays(3),
                    OffsetDateTime.now().plusDays(5)
                )
            ),
            Arguments.of(
                "startDate",
                "Start date must be in the future",
                new BookPropertyRequest(
                    randomAlphanumeric(30),
                    randomAlphanumeric(50),
                    OffsetDateTime.now().minusDays(1),
                    OffsetDateTime.now().plusDays(5)
                )
            ),
            Arguments.of(
                "endDate",
                "End date must be in the future",
                new BookPropertyRequest(
                    randomAlphanumeric(31),
                    randomAlphanumeric(50),
                    OffsetDateTime.now().plusDays(3),
                    OffsetDateTime.now().minusDays(1)
                )
            )
        );
      }
    }
  }

  @Nested
  class DeleteBookingTests {

    @Test
    void happyPath() throws Exception {
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
      PropertyBookingEntity propertyBookingEntity = PropertyBookingEntityMother.complete(propertyId);
      propertyBookingsTestDataHelper.insertPropertyBooking(propertyBookingEntity);

      String url = String.format(CONTROLLER_URL, propertyId) + "/" + propertyBookingEntity.id();

      delete(HttpStatus.NO_CONTENT, url)
          .andReturn();
    }

    @Test
    void propertyDoesNotExist_shouldNotCancelBooking() throws Exception {
      UUID notExistingPropertyId = UUID.randomUUID();
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
      PropertyBookingEntity propertyBookingEntity = PropertyBookingEntityMother.complete(propertyId);
      propertyBookingsTestDataHelper.insertPropertyBooking(propertyBookingEntity);

      String url = String.format(CONTROLLER_URL, propertyId) + "/" + notExistingPropertyId;

      delete(HttpStatus.NOT_FOUND, url)
          .andExpect(
              jsonPath("$.message", equalTo("Property booking not found"))
          );
    }

    @Test
    void propertyBookingAlreadyCanceled_shouldNotCancelBooking() throws Exception {
      OffsetDateTime dateDeleted = OffsetDateTime.now();
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
      PropertyBookingEntity propertyBookingEntity = PropertyBookingEntityMother.complete(propertyId);
      propertyBookingsTestDataHelper.insertPropertyBooking(propertyBookingEntity);
      propertyBookingsTestDataHelper.cancelBooking(propertyBookingEntity.id(), dateDeleted);

      String url = String.format(CONTROLLER_URL, propertyId) + "/" + propertyBookingEntity.id();

      delete(HttpStatus.NOT_FOUND, url)
          .andExpect(
              jsonPath("$.message", equalTo("Property booking not found"))
          );
    }
  }
}
