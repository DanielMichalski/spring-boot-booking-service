package com.danielmichalski.bookingservice.property.repository.helper;

import com.danielmichalski.bookingservice.property.entity.PropertyBookingEntity;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyBookingsTestDataHelper {

  private final NamedParameterJdbcTemplate namedJdbcTemplate;

  public UUID insertPropertyBooking(PropertyBookingEntity entity) {
    String sql = """
            INSERT INTO property_bookings
                 (id, guest_first_name, guest_last_name, date_created, start_date, end_date, property_id)
             VALUES
                 (:id, :guestFirstName, :guestLastName, :dateCreated, :startDate, :endDate, :propertyId)
        """;

    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("id", entity.id())
        .addValue("guestFirstName", entity.guestFirstName())
        .addValue("guestLastName", entity.guestLastName())
        .addValue("dateCreated", entity.dateCreated())
        .addValue("startDate", entity.startDate())
        .addValue("endDate", entity.endDate())
        .addValue("propertyId", entity.propertyId());

    namedJdbcTemplate.update(sql, parameters);
    return entity.id();
  }

  public void cancelBooking(UUID bookingId, OffsetDateTime dateDeleted) {
    String sql = """
          UPDATE property_bookings
          SET date_deleted = :dateDeleted
          WHERE id = :bookingId
        """;

    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("bookingId", bookingId)
        .addValue("dateDeleted", dateDeleted);

    namedJdbcTemplate.update(sql, parameters);
  }

  public PropertyBookingEntity getPropertyBooking(UUID propertyBookingId) {
    String sql = """
            SELECT id, guest_first_name, guest_last_name, date_created, start_date, end_date, property_id
            FROM property_bookings
            WHERE id = :id
                AND date_deleted IS NULL
        """;
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", propertyBookingId);
    return namedJdbcTemplate.queryForObject(sql, params, new DataClassRowMapper<>(PropertyBookingEntity.class));
  }

}
