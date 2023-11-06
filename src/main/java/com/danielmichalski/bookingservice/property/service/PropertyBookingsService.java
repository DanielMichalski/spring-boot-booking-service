package com.danielmichalski.bookingservice.property.service;

import com.danielmichalski.bookingservice.common.date.CurrentDateTimeService;
import com.danielmichalski.bookingservice.common.exception.NotFoundException;
import com.danielmichalski.bookingservice.property.dto.BookPropertyRequest;
import com.danielmichalski.bookingservice.property.dto.PropertyBookingDto;
import com.danielmichalski.bookingservice.property.dto.UpdateBookingRequest;
import com.danielmichalski.bookingservice.property.entity.PropertyBookingEntity;
import com.danielmichalski.bookingservice.property.mapper.PropertyBookingMapper;
import com.danielmichalski.bookingservice.property.repository.PropertyBookingsRepository;
import com.danielmichalski.bookingservice.property.validator.PropertyBookingsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyBookingsService {

    private final PropertyBookingsRepository propertyBookingsRepository;
    private final CurrentDateTimeService currentDateTimeService;
    private final PropertyBookingsValidator propertyBookingsValidator;

    @Transactional
    public PropertyBookingDto bookProperty(UUID propertyId, BookPropertyRequest request) {
        propertyBookingsValidator.validateBooking(propertyId, request.startDate(), request.endDate());

        UUID bookingId = UUID.randomUUID();
        PropertyBookingEntity propertyBookingEntity = new PropertyBookingEntity(
                bookingId,
                request.guestFirstName(),
                request.questLastName(),
                currentDateTimeService.currentDateTime(),
                request.startDate(),
                request.endDate(),
                propertyId
        );

        propertyBookingsRepository.bookProperty(propertyBookingEntity);

        return PropertyBookingMapper.mapTaskDto(propertyBookingEntity);
    }

    @Transactional
    public void updateBooking(UUID propertyId, UUID bookingId, UpdateBookingRequest request) {
        propertyBookingsValidator.validateBooking(propertyId, request.startDate(), request.endDate());

        PropertyBookingEntity originalEntity = propertyBookingsRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        PropertyBookingEntity updatedEntity = new PropertyBookingEntity(
                originalEntity.id(),
                request.guestFirstName(),
                request.questLastName(),
                originalEntity.dateCreated(),
                request.startDate(),
                request.endDate(),
                originalEntity.propertyId()
        );

        propertyBookingsRepository.updateBooking(updatedEntity);
    }

    @Transactional
    public void cancelBooking(UUID propertyId, UUID bookingId) {
        boolean bookingCanceled = propertyBookingsRepository.cancelBooking(propertyId, bookingId);

        if (!bookingCanceled) {
            throw new NotFoundException("Property booking not found");
        }
    }

}
