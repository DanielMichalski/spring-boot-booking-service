package com.danielmichalski.bookingservice.property.repository.helper;

import com.danielmichalski.bookingservice.property.entity.PropertyBlockEntity;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyBlocksTestDataHelper {

  private final NamedParameterJdbcTemplate namedJdbcTemplate;

  public UUID insertPropertyBlock(PropertyBlockEntity entity) {
    String sql = """
            INSERT INTO property_blocks
                 (id, date_created, start_date, end_date, property_id)
             VALUES
                 (:id, :dateCreated, :startDate, :endDate, :propertyId)
        """;

    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("id", entity.id())
        .addValue("dateCreated", entity.dateCreated())
        .addValue("startDate", entity.startDate())
        .addValue("endDate", entity.endDate())
        .addValue("propertyId", entity.propertyId());

    namedJdbcTemplate.update(sql, parameters);
    return entity.id();
  }

  public void cancelPropertyBlock(UUID propertyBlockId, OffsetDateTime dateDeleted) {
    String sql = """
          UPDATE property_blocks
          SET date_deleted = :dateDeleted
          WHERE id = :propertyBlockId
        """;

    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("propertyBlockId", propertyBlockId)
        .addValue("dateDeleted", dateDeleted);

    namedJdbcTemplate.update(sql, parameters);
  }

  public PropertyBlockEntity getPropertyBlock(UUID propertyBlockId) {
    String sql = """
            SELECT id, date_created, start_date, end_date, property_id
            FROM property_blocks
            WHERE id = :id
                AND date_deleted IS NULL
        """;
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", propertyBlockId);
    return namedJdbcTemplate.queryForObject(sql, params, new DataClassRowMapper<>(PropertyBlockEntity.class));
  }

}
