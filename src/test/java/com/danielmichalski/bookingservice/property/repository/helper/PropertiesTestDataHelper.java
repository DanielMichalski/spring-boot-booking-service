package com.danielmichalski.bookingservice.property.repository.helper;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertiesTestDataHelper {

  private final NamedParameterJdbcTemplate namedJdbcTemplate;

  public UUID insertProperty(String name, String city) {
    UUID propertyId = UUID.randomUUID();

    String sql = """
            INSERT INTO properties (id, name, city)
            VALUES (:propertyId, :name, :city)
        """;

    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("propertyId", propertyId)
        .addValue("name", name)
        .addValue("city", city);

    namedJdbcTemplate.update(sql, parameters);
    return propertyId;
  }

}
