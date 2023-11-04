package com.danielmichalski.bookingservice.common.date;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class CurrentDateTimeService {

    public OffsetDateTime currentDateTime() {
        return OffsetDateTime.now();
    }

}
