package com.danielmichalski.bookingservice.property.controller;

import com.danielmichalski.bookingservice.property.dto.BookPropertyRequest;
import com.danielmichalski.bookingservice.property.dto.UpdateBookingRequest;
import com.danielmichalski.bookingservice.property.service.PropertyBookingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/properties/{propertyId}/bookings")
@RequiredArgsConstructor
public class PropertyBookingsController {

    private final PropertyBookingsService propertyBookingsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID bookProperty(@PathVariable UUID propertyId,
                             @Valid @RequestBody BookPropertyRequest request) {
        return propertyBookingsService.bookProperty(propertyId, request);
    }

    @PutMapping("/{bookingId}")
    @ResponseStatus(NO_CONTENT)
    public void updateBooking(@PathVariable UUID propertyId,
                              @PathVariable UUID bookingId,
                              @Valid @RequestBody UpdateBookingRequest request) {
        propertyBookingsService.updateBooking(propertyId, bookingId, request);
    }

    @DeleteMapping("/{bookingId}")
    public void cancelBooking(@PathVariable UUID propertyId, @PathVariable UUID bookingId) {
        propertyBookingsService.cancelBooking(propertyId, bookingId);
    }

}
