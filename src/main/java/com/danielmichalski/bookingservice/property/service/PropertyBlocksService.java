package com.danielmichalski.bookingservice.property.service;

import com.danielmichalski.bookingservice.common.date.CurrentDateTimeService;
import com.danielmichalski.bookingservice.common.exception.NotFoundException;
import com.danielmichalski.bookingservice.property.dto.BlockPropertyRequest;
import com.danielmichalski.bookingservice.property.dto.PropertyBlockDto;
import com.danielmichalski.bookingservice.property.dto.UpdateBlockRequest;
import com.danielmichalski.bookingservice.property.entity.PropertyBlockEntity;
import com.danielmichalski.bookingservice.property.mapper.PropertyBlockMapper;
import com.danielmichalski.bookingservice.property.repository.PropertyBlocksRepository;
import com.danielmichalski.bookingservice.property.validator.PropertyBlocksValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PropertyBlocksService {

  private final PropertyBlocksRepository propertyBlocksRepository;
  private final CurrentDateTimeService currentDateTimeService;
  private final PropertyBlocksValidator propertyBlocksValidator;

  @Transactional
  public PropertyBlockDto blockProperty(UUID propertyId, BlockPropertyRequest request) {
    propertyBlocksValidator.validateBlock(propertyId, request.startDate(), request.endDate());

    UUID blockId = UUID.randomUUID();
    PropertyBlockEntity propertyBlockEntity = new PropertyBlockEntity(
        blockId,
        currentDateTimeService.currentDateTime(),
        request.startDate(),
        request.endDate(),
        propertyId
    );

    propertyBlocksRepository.blockProperty(propertyBlockEntity);

    return PropertyBlockMapper.mapTaskDto(propertyBlockEntity);
  }

  @Transactional
  public void updateBlock(UUID propertyId, UUID blockId, UpdateBlockRequest request) {
    propertyBlocksValidator.validateBlock(propertyId, request.startDate(), request.endDate());

    PropertyBlockEntity originalEntity = propertyBlocksRepository.findById(blockId)
        .orElseThrow(() -> new NotFoundException("Property block not found"));

    PropertyBlockEntity updatedEntity = new PropertyBlockEntity(
        originalEntity.id(),
        originalEntity.dateCreated(),
        request.startDate(),
        request.endDate(),
        originalEntity.propertyId()
    );

    propertyBlocksRepository.updateBlock(updatedEntity);
  }

  @Transactional
  public void cancelBlock(UUID propertyId, UUID blockId) {
    boolean blockCanceled = propertyBlocksRepository.cancelBlock(propertyId, blockId);

    if (!blockCanceled) {
      throw new NotFoundException("Property block not found");
    }
  }

}
