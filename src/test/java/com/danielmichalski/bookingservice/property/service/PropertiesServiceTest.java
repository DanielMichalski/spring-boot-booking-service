package com.danielmichalski.bookingservice.property.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.danielmichalski.bookingservice.common.exception.NotFoundException;
import com.danielmichalski.bookingservice.property.repository.PropertiesRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertiesServiceTest {

  @Mock
  private PropertiesRepository propertiesRepository;
  @InjectMocks
  private PropertiesService underTest;


  @Nested
  class ValidatePropertyExistsTests {

    private UUID propertyId;

    @BeforeEach
    void setUp() {
      propertyId = UUID.randomUUID();
    }

    @Test
    void propertyExists_shouldNotThrowAnyException() {
      when(propertiesRepository.propertyExists(propertyId)).thenReturn(true);

      assertThatNoException()
          .isThrownBy(
              () -> underTest.validatePropertyExists(propertyId)
          );
    }

    @Test
    void propertyDoesntExist_shouldThrowAnException() {
      when(propertiesRepository.propertyExists(propertyId)).thenReturn(false);

      assertThrows(
          NotFoundException.class,
          () -> underTest.validatePropertyExists(propertyId),
          "Property not found"
      );
    }
  }

}
