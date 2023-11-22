package com.danielmichalski.bookingservice.common.date;

import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class CurrentDateTimeService {

  public OffsetDateTime currentDateTime() {
    return OffsetDateTime.now();
  }

}
