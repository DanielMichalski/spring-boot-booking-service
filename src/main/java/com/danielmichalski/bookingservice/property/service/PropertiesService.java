package com.danielmichalski.bookingservice.property.service;

import com.danielmichalski.bookingservice.common.exception.NotFoundException;
import com.danielmichalski.bookingservice.property.repository.PropertiesRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertiesService {

  private final PropertiesRepository propertiesRepository;

  public void validatePropertyExists(UUID propertyId) {
    if (!propertiesRepository.propertyExists(propertyId)) {
      throw new NotFoundException("Property not found");
    }
  }

}
