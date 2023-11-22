package com.danielmichalski.bookingservice.common.date;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CurrentDateTimeServiceTest {

  private CurrentDateTimeService underTest;

  @BeforeEach
  void setUp() {
    underTest = new CurrentDateTimeService();
  }

  @Test
  void shouldReturnCurrentDate() {
    assertThat(underTest.currentDateTime()).isNotNull();
  }
}
