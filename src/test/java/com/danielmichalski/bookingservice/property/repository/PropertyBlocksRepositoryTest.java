package com.danielmichalski.bookingservice.property.repository;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.danielmichalski.bookingservice.property.entity.PropertyBlockEntity;
import com.danielmichalski.bookingservice.property.repository.helper.PropertiesTestDataHelper;
import com.danielmichalski.bookingservice.property.repository.helper.PropertyBlocksTestDataHelper;
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
class PropertyBlocksRepositoryTest {

  @Autowired
  private PropertiesTestDataHelper propertiesTestDataHelper;
  @Autowired
  private PropertyBlocksTestDataHelper propertyBlocksTestDataHelper;
  @Autowired
  private PropertyBlocksRepository underTest;

  @Nested
  class FindByIdTests {

    @Test
    void propertyBlocksNotExists_shouldReturnEmptyOptional() {
      UUID notExistingPropertyId = UUID.randomUUID();

      Optional<PropertyBlockEntity> result = underTest.findById(notExistingPropertyId);

      assertThat(result).isNotPresent();
    }

    @Test
    void propertyBlocksExists_shouldReturnOptionalWithAnEntity() {
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

      PropertyBlockEntity entity = new PropertyBlockEntity(
          UUID.randomUUID(),
          OffsetDateTime.now(),
          startDate,
          endDate,
          propertyId
      );
      UUID returnedPropertyBlocksId = propertyBlocksTestDataHelper.insertPropertyBlock(entity);

      Optional<PropertyBlockEntity> result = underTest.findById(returnedPropertyBlocksId);

      assertThat(result)
          .isPresent()
          .get()
          .isEqualTo(entity);
    }
  }

  @Nested
  class BlocksExistsWithinRangeTests {

    private UUID propertyId;
    private static final OffsetDateTime EXISTING_BLOCK_START_DATE = OffsetDateTime.now()
        .plusDays(5)
        .truncatedTo(ChronoUnit.DAYS);
    private static final OffsetDateTime EXISTING_BLOCK_END_DATE = OffsetDateTime.now()
        .plusDays(10)
        .truncatedTo(ChronoUnit.DAYS);

    @BeforeEach
    void setUp() {
      propertyId = propertiesTestDataHelper.insertProperty(
          randomAlphanumeric(10),
          randomAlphanumeric(7)
      );

      PropertyBlockEntity entity = new PropertyBlockEntity(
          UUID.randomUUID(),
          OffsetDateTime.now(),
          EXISTING_BLOCK_START_DATE,
          EXISTING_BLOCK_END_DATE,
          propertyId
      );
      propertyBlocksTestDataHelper.insertPropertyBlock(entity);
    }

    @ParameterizedTest(name = "[{index}] => [{0}], startDate={1}, endDate={1}")
    @MethodSource("provideRangesWithingExistingBlocksRange")
    void blockAlreadyExistsWithingTheRange_shouldReturnTrue(String testCaseDescription,
                                                            OffsetDateTime startDate,
                                                            OffsetDateTime endDate) {
      boolean result = underTest.blockExistsWithinRange(propertyId, startDate, endDate);

      assertThat(result)
          .as(testCaseDescription)
          .isTrue();
    }

    @ParameterizedTest(name = "[{index}] => [{0}], startDate={1}, endDate={1}")
    @MethodSource("provideRangesNotWithingExistingBlocksRange")
    void blockNotExistsWithingTheRange_shouldReturnFalse(String testCaseDescription,
                                                         OffsetDateTime startDate,
                                                         OffsetDateTime endDate) {
      boolean result = underTest.blockExistsWithinRange(propertyId, startDate, endDate);

      assertThat(result)
          .as(testCaseDescription)
          .isFalse();
    }

    private static Stream<Arguments> provideRangesWithingExistingBlocksRange() {
      return Stream.of(
          Arguments.of(
              "startDate before existing startDate and endDate after existing endDate",
              EXISTING_BLOCK_START_DATE.minusDays(3),
              EXISTING_BLOCK_END_DATE.plusDays(4)
          ),
          Arguments.of(
              "startDate before existing startDate and endDate before existing endDate",
              EXISTING_BLOCK_START_DATE.minusDays(2),
              EXISTING_BLOCK_END_DATE.minusDays(1)
          ),
          Arguments.of(
              "startDate after existing startDate and endDate before existing endDate",
              EXISTING_BLOCK_START_DATE.plusDays(1),
              EXISTING_BLOCK_END_DATE.minusDays(2)
          ),
          Arguments.of(
              "startDate after existing startDate and endDate after existing endDate",
              EXISTING_BLOCK_START_DATE.plusDays(2),
              EXISTING_BLOCK_END_DATE.plusDays(4)
          )
      );
    }

    private static Stream<Arguments> provideRangesNotWithingExistingBlocksRange() {
      return Stream.of(
          Arguments.of(
              "startDate before existing startDate and endDate before existing startDate",
              EXISTING_BLOCK_START_DATE.minusDays(3),
              EXISTING_BLOCK_START_DATE.minusDays(1)
          ),
          Arguments.of(
              "startDate after existing endDate and endDate after existing endDate",
              EXISTING_BLOCK_END_DATE.plusDays(1),
              EXISTING_BLOCK_END_DATE.plusDays(3)
          )
      );
    }
  }

  @Test
  void bookProperty_shouldInsertRecord() {
    UUID propertyBlocksId = UUID.randomUUID();
    OffsetDateTime dateCreated = OffsetDateTime.now();
    OffsetDateTime startDate = OffsetDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
    OffsetDateTime endDate = OffsetDateTime.now().plusDays(3).truncatedTo(ChronoUnit.DAYS);

    UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(7), randomAlphanumeric(10));
    PropertyBlockEntity propertyBlocksEntity = new PropertyBlockEntity(
        propertyBlocksId,
        dateCreated,
        startDate,
        endDate,
        propertyId
    );

    underTest.blockProperty(propertyBlocksEntity);

    PropertyBlockEntity storedPropertyBlocks = propertyBlocksTestDataHelper.getPropertyBlock(propertyBlocksId);
    assertThat(storedPropertyBlocks).isEqualTo(propertyBlocksEntity);
  }

  @Test
  void updateBlocks_shouldUpdateRecord() {
    UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(7), randomAlphanumeric(10));
    PropertyBlockEntity blockEntity = new PropertyBlockEntity(
        UUID.randomUUID(),
        OffsetDateTime.now(),
        OffsetDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS),
        OffsetDateTime.now().plusDays(3).truncatedTo(ChronoUnit.DAYS),
        propertyId
    );
    UUID propertyBlocksId = propertyBlocksTestDataHelper.insertPropertyBlock(blockEntity);

    PropertyBlockEntity blockEntityToUpdate = new PropertyBlockEntity(
        blockEntity.id(),
        blockEntity.dateCreated(),
        OffsetDateTime.now().plusDays(6).truncatedTo(ChronoUnit.DAYS),
        OffsetDateTime.now().plusDays(10).truncatedTo(ChronoUnit.DAYS),
        blockEntity.propertyId()
    );

    underTest.updateBlock(blockEntityToUpdate);

    PropertyBlockEntity storedPropertyBlocks = propertyBlocksTestDataHelper.getPropertyBlock(propertyBlocksId);
    assertThat(storedPropertyBlocks)
        .extracting(
            PropertyBlockEntity::id,
            PropertyBlockEntity::dateCreated,
            PropertyBlockEntity::startDate,
            PropertyBlockEntity::endDate,
            PropertyBlockEntity::propertyId
        ).containsExactly(
            blockEntity.id(),
            blockEntity.dateCreated(),
            blockEntityToUpdate.startDate(),
            blockEntityToUpdate.endDate(),
            blockEntity.propertyId()
        );
  }

  @Test
  @Transactional
  void cancelBlocks_shouldUpdateTheRecordBySettingDateDeleted() {
    UUID propertyId = propertiesTestDataHelper.insertProperty(randomAlphanumeric(7), randomAlphanumeric(10));
    UUID propertyBlocksId = UUID.randomUUID();
    PropertyBlockEntity propertyBlocks = new PropertyBlockEntity(
        propertyBlocksId,
        OffsetDateTime.now(),
        OffsetDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS),
        OffsetDateTime.now().plusDays(3).truncatedTo(ChronoUnit.DAYS),
        propertyId
    );
    propertyBlocksTestDataHelper.insertPropertyBlock(propertyBlocks);
    propertyBlocks = propertyBlocksTestDataHelper.getPropertyBlock(propertyBlocksId);
    assertThat(propertyBlocks).isNotNull()
        .extracting(PropertyBlockEntity::id)
        .isEqualTo(propertyBlocksId);

    underTest.cancelBlock(propertyId, propertyBlocksId);

    assertThrows(
        EmptyResultDataAccessException.class,
        () -> propertyBlocksTestDataHelper.getPropertyBlock(propertyBlocksId)
    );
  }


}
