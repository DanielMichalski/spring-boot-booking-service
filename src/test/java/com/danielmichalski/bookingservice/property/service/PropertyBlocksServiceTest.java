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
import com.danielmichalski.bookingservice.property.dto.BlockPropertyRequest;
import com.danielmichalski.bookingservice.property.dto.PropertyBlockDto;
import com.danielmichalski.bookingservice.property.dto.UpdateBlockRequest;
import com.danielmichalski.bookingservice.property.entity.PropertyBlockEntity;
import com.danielmichalski.bookingservice.property.repository.PropertyBlocksRepository;
import com.danielmichalski.bookingservice.property.validator.PropertyBlocksValidator;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyBlocksServiceTest {

  @Mock
  private PropertyBlocksRepository propertyBlocksRepository;
  @Mock
  private CurrentDateTimeService currentDateTimeService;
  @Mock
  private PropertyBlocksValidator propertyBlocksValidator;
  @InjectMocks
  private PropertyBlocksService underTest;

  @Nested
  class BlockPropertyTests {

    private UUID propertyId;
    private OffsetDateTime currentDateTime;
    private BlockPropertyRequest blockPropertyRequest;

    @BeforeEach
    void setUp() {
      OffsetDateTime startDate = OffsetDateTime.now().plusDays(5);
      OffsetDateTime endDate = OffsetDateTime.now().plusDays(1);

      propertyId = UUID.randomUUID();
      currentDateTime = OffsetDateTime.now();
      blockPropertyRequest = new BlockPropertyRequest(
          startDate,
          endDate
      );
    }

    @Test
    void happyPath() {
      when(currentDateTimeService.currentDateTime()).thenReturn(currentDateTime);

      PropertyBlockDto returnedDto = underTest.blockProperty(propertyId, blockPropertyRequest);

      ArgumentCaptor<PropertyBlockEntity> entityToStoreCaptor =
          ArgumentCaptor.forClass(PropertyBlockEntity.class);
      verify(propertyBlocksRepository).blockProperty(entityToStoreCaptor.capture());
      assertThat(entityToStoreCaptor.getValue())
          .extracting(
              (entity) -> Objects.nonNull(entity.id()),
              PropertyBlockEntity::dateCreated,
              PropertyBlockEntity::startDate,
              PropertyBlockEntity::endDate,
              PropertyBlockEntity::propertyId
          ).containsExactly(
              true,
              currentDateTime,
              blockPropertyRequest.startDate(),
              blockPropertyRequest.endDate(),
              propertyId
          );
      assertThat(returnedDto)
          .extracting(
              (dto) -> Objects.nonNull(dto.id()),
              PropertyBlockDto::dateCreated,
              PropertyBlockDto::startDate,
              PropertyBlockDto::endDate,
              PropertyBlockDto::propertyId
          ).containsExactly(
              true,
              currentDateTime,
              blockPropertyRequest.startDate(),
              blockPropertyRequest.endDate(),
              propertyId
          );
    }

    @Test
    void validatorTrowsAnException_shouldNotBookProperty() {
      IllegalArgumentException exception = new IllegalArgumentException("Block property exception");
      doThrow(exception)
          .when(propertyBlocksValidator)
          .validateBlock(propertyId, blockPropertyRequest.startDate(), blockPropertyRequest.endDate());

      assertThrows(
          exception.getClass(),
          () -> underTest.blockProperty(propertyId, blockPropertyRequest),
          exception.getMessage()
      );
      verifyNoInteractions(propertyBlocksRepository);
    }
  }

  @Nested
  class UpdateBlockTests {

    private UUID propertyId;
    private UUID blockId;
    private UpdateBlockRequest updateBlockRequest;

    @BeforeEach
    void setUp() {
      OffsetDateTime startDate = OffsetDateTime.now().plusDays(1);
      OffsetDateTime endDate = OffsetDateTime.now().plusDays(3);

      propertyId = UUID.randomUUID();
      blockId = UUID.randomUUID();
      updateBlockRequest = new UpdateBlockRequest(
          startDate,
          endDate
      );
    }

    @Test
    void happyPath() {
      UUID id = UUID.randomUUID();
      OffsetDateTime dateCreated = OffsetDateTime.now();
      OffsetDateTime startDate = OffsetDateTime.now().minusDays(1);
      OffsetDateTime endDate = OffsetDateTime.now().minusDays(3);
      PropertyBlockEntity originalEntity = new PropertyBlockEntity(
          id,
          dateCreated,
          startDate,
          endDate,
          propertyId
      );

      when(propertyBlocksRepository.findById(blockId)).thenReturn(Optional.of(originalEntity));

      underTest.updateBlock(propertyId, blockId, updateBlockRequest);

      ArgumentCaptor<PropertyBlockEntity> entityToStoreCaptor =
          ArgumentCaptor.forClass(PropertyBlockEntity.class);
      verify(propertyBlocksRepository).updateBlock(entityToStoreCaptor.capture());
      assertThat(entityToStoreCaptor.getValue())
          .extracting(
              PropertyBlockEntity::id,
              PropertyBlockEntity::dateCreated,
              PropertyBlockEntity::startDate,
              PropertyBlockEntity::endDate,
              PropertyBlockEntity::propertyId
          ).containsExactly(
              id,
              dateCreated,
              updateBlockRequest.startDate(),
              updateBlockRequest.endDate(),
              propertyId
          );
    }

    @Test
    void validatorTrowsAnException_shouldNotUpdateBlock() {
      IllegalArgumentException exception = new IllegalArgumentException("Blocking exception");
      doThrow(exception)
          .when(propertyBlocksValidator)
          .validateBlock(propertyId, updateBlockRequest.startDate(), updateBlockRequest.endDate());

      assertThrows(
          exception.getClass(),
          () -> underTest.updateBlock(propertyId, blockId, updateBlockRequest),
          exception.getMessage()
      );
      verifyNoInteractions(propertyBlocksRepository);
    }

    @Test
    void propertyBlockEntityNotFound_shouldThrowNotFoundException_andNotUpdateBlock() {
      when(propertyBlocksRepository.findById(blockId)).thenReturn(Optional.empty());

      assertThrows(
          NotFoundException.class,
          () -> underTest.updateBlock(propertyId, blockId, updateBlockRequest),
          "Property block not found"
      );
      verifyNoMoreInteractions(propertyBlocksRepository);
    }
  }

  @Nested
  class CancelBlockTests {

    @Test
    void happyPath() {
      UUID propertyId = UUID.randomUUID();
      UUID blockId = UUID.randomUUID();
      when(propertyBlocksRepository.cancelBlock(propertyId, blockId)).thenReturn(false);

      assertThrows(
          NotFoundException.class,
          () -> underTest.cancelBlock(propertyId, blockId),
          "Property block not found"
      );
    }

    @Test
    void blockNotCancelled_shouldThrowAnException() {
      UUID propertyId = UUID.randomUUID();
      UUID blockId = UUID.randomUUID();
      when(propertyBlocksRepository.cancelBlock(propertyId, blockId)).thenReturn(true);

      assertThatNoException()
          .isThrownBy(
              () -> underTest.cancelBlock(propertyId, blockId)
          );
    }
  }

}
