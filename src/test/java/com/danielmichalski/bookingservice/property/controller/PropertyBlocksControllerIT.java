package com.danielmichalski.bookingservice.property.controller;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.danielmichalski.bookingservice.controller.ControllerTestBase;
import com.danielmichalski.bookingservice.property.dto.BlockPropertyRequest;
import com.danielmichalski.bookingservice.property.dto.UpdateBlockRequest;
import com.danielmichalski.bookingservice.property.entity.PropertyBlockEntity;
import com.danielmichalski.bookingservice.property.mother.BlockPropertyRequestMother;
import com.danielmichalski.bookingservice.property.mother.PropertyBlockEntityMother;
import com.danielmichalski.bookingservice.property.repository.helper.PropertiesTestDataHelper;
import com.danielmichalski.bookingservice.property.repository.helper.PropertyBlocksTestDataHelper;
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
class PropertyBlocksControllerIT extends ControllerTestBase {

  private static final String CONTROLLER_URL = "/api/properties/%s/blocks";

  @Autowired
  private PropertiesTestDataHelper propertiesTestDataHelper;
  @Autowired
  private PropertyBlocksTestDataHelper propertyBlocksTestDataHelper;

  @Nested
  class BlockPropertyTests {

    @Test
    void happyPath() throws Exception {
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
      BlockPropertyRequest request = BlockPropertyRequestMother.complete();

      String url = String.format(CONTROLLER_URL, propertyId);

      post(request, HttpStatus.CREATED, url)
          .andExpectAll(
              jsonPath("$.id", notNullValue()),
              jsonPath("$.dateCreated", notNullValue()),
              jsonPath("$.startDate", equalTo(formatDateToJsonDate(request.startDate()))),
              jsonPath("$.endDate", equalTo(formatDateToJsonDate(request.endDate()))),
              jsonPath("$.propertyId", equalTo(propertyId.toString()))
          );
    }

    @Test
    void propertyDoesNotExist_shouldNotBlockProperty() throws Exception {
      UUID notExistingPropertyId = UUID.randomUUID();
      BlockPropertyRequest request = BlockPropertyRequestMother.complete();

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
                               BlockPropertyRequest request) throws Exception {
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
                "startDate",
                "Start date must be in the future",
                new BlockPropertyRequest(
                    OffsetDateTime.now().minusDays(1),
                    OffsetDateTime.now().plusDays(5)
                )
            ),
            Arguments.of(
                "endDate",
                "End date must be in the future",
                new BlockPropertyRequest(
                    OffsetDateTime.now().plusDays(3),
                    OffsetDateTime.now().minusDays(1)
                )
            )
        );
      }
    }
  }

  @Nested
  class UpdateBlockTests {

    @Test
    void happyPath() throws Exception {
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
      PropertyBlockEntity propertyBlockEntity = PropertyBlockEntityMother.complete(propertyId);
      propertyBlocksTestDataHelper.insertPropertyBlock(propertyBlockEntity);

      UpdateBlockRequest request = new UpdateBlockRequest(
          OffsetDateTime.now().plusDays(10),
          OffsetDateTime.now().plusDays(14)
      );

      String url = String.format(CONTROLLER_URL, propertyId) + "/" + propertyBlockEntity.id();
      put(request, HttpStatus.NO_CONTENT, url)
          .andReturn();
    }

    @Test
    void propertyDoesNotExist_shouldNotUpdateProperty() throws Exception {
      UUID notExistingPropertyId = UUID.randomUUID();
      UUID blockId = UUID.randomUUID();
      UpdateBlockRequest request = new UpdateBlockRequest(
          OffsetDateTime.now().plusDays(1),
          OffsetDateTime.now().plusDays(2)
      );

      String url = String.format(CONTROLLER_URL, notExistingPropertyId) + "/" + blockId;

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
                               BlockPropertyRequest request) throws Exception {
        UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
        UUID blockId = UUID.randomUUID();
        String url = String.format(CONTROLLER_URL, propertyId) + "/" + blockId;

        put(request, HttpStatus.BAD_REQUEST, url)
            .andExpect(
                jsonPath(String.format("$.%s", fieldName), equalTo(fieldValidationMessage))
            );
      }

      private static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
            Arguments.of(
                "startDate",
                "Start date must be in the future",
                new BlockPropertyRequest(
                    OffsetDateTime.now().minusDays(1),
                    OffsetDateTime.now().plusDays(5)
                )
            ),
            Arguments.of(
                "endDate",
                "End date must be in the future",
                new BlockPropertyRequest(
                    OffsetDateTime.now().plusDays(3),
                    OffsetDateTime.now().minusDays(1)
                )
            )
        );
      }
    }
  }

  @Nested
  class DeleteBlockTests {

    @Test
    void happyPath() throws Exception {
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
      PropertyBlockEntity propertyBlockEntity = PropertyBlockEntityMother.complete(propertyId);
      propertyBlocksTestDataHelper.insertPropertyBlock(propertyBlockEntity);

      String url = String.format(CONTROLLER_URL, propertyId) + "/" + propertyBlockEntity.id();

      delete(HttpStatus.NO_CONTENT, url)
          .andReturn();
    }

    @Test
    void propertyDoesNotExist_shouldNotCancelBlock() throws Exception {
      UUID notExistingPropertyId = UUID.randomUUID();
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
      PropertyBlockEntity propertyBlockEntity = PropertyBlockEntityMother.complete(propertyId);
      propertyBlocksTestDataHelper.insertPropertyBlock(propertyBlockEntity);

      String url = String.format(CONTROLLER_URL, propertyId) + "/" + notExistingPropertyId;

      delete(HttpStatus.NOT_FOUND, url)
          .andExpect(
              jsonPath("$.message", equalTo("Property block not found"))
          );
    }

    @Test
    void propertyBlockAlreadyCanceled_shouldNotCancelBlock() throws Exception {
      OffsetDateTime dateDeleted = OffsetDateTime.now();
      UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(3), randomAlphanumeric(5));
      PropertyBlockEntity propertyBlockEntity = PropertyBlockEntityMother.complete(propertyId);
      propertyBlocksTestDataHelper.insertPropertyBlock(propertyBlockEntity);
      propertyBlocksTestDataHelper.cancelPropertyBlock(propertyBlockEntity.id(), dateDeleted);

      String url = String.format(CONTROLLER_URL, propertyId) + "/" + propertyBlockEntity.id();

      delete(HttpStatus.NOT_FOUND, url)
          .andExpect(
              jsonPath("$.message", equalTo("Property block not found"))
          );
    }
  }


}
