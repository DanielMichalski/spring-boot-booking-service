package com.danielmichalski.bookingservice.property.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.danielmichalski.bookingservice.property.repository.helper.PropertiesTestDataHelper;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PropertiesRepositoryTest {

  @Autowired
  private PropertiesTestDataHelper propertiesTestDataHelper;
  @Autowired
  private PropertiesRepository underTest;

  @Nested
  class InsertPropertyTests {

    @Test
    void notExistingProperty_shouldReturnFalse() {
      UUID notExistingPropertyId = UUID.randomUUID();

      boolean result = underTest.propertyExists(notExistingPropertyId);

      assertThat(result).isFalse();
    }

    @Test
    void existingProperty_shouldReturnTrue() {
      String name = "test";
      String city = "test";
      UUID existingPropertyId = propertiesTestDataHelper.insertProperty(name, city);

      boolean result = underTest.propertyExists(existingPropertyId);

      assertThat(result).isTrue();
    }
  }
}
