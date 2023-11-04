package com.danielmichalski.bookingservice.property.repository;

import com.danielmichalski.bookingservice.property.entity.PropertyBlockEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Mapper
@Repository
public interface PropertyBlocksRepository {

    @Select("""
                SELECT id, date_created, start_date, end_date, property_id
                FROM property_blocks
                WHERE id = #{blockId}
                    AND date_deleted IS NULL
            """)
    Optional<PropertyBlockEntity> findById(@Param("blockId") UUID blockId);

    @Select("""
                SELECT EXISTS(
                    SELECT 1 FROM property_blocks
                    WHERE property_id = #{propertyId}
                        AND date_deleted IS NULL
                        AND (
                            #{startDate} BETWEEN start_date AND end_date
                            OR #{endDate} BETWEEN start_date AND end_date
                            OR start_date BETWEEN #{startDate} AND #{endDate}
                        )
                )
            """)
    boolean blockExistsWithinRange(@Param("propertyId") UUID propertyId,
                                   @Param("startDate") OffsetDateTime startDate,
                                   @Param("endDate") OffsetDateTime endDate);

    @Insert("""
                INSERT INTO property_blocks (id, date_created, start_date, end_date, property_id)
                VALUES (#{id}, #{dateCreated}, #{startDate}, #{endDate}, #{propertyId})
            """)
    void blockProperty(PropertyBlockEntity propertyBlockEntity);

    @Update("""
                UPDATE property_blocks
                SET start_date = #{startDate},
                    end_date = #{endDate},
                    date_updated = NOW()
                WHERE id = #{id}
                    AND date_deleted IS NULL
            """)
    void updateBlock(PropertyBlockEntity updatedEntity);

    @Update("""
                UPDATE property_blocks
                SET date_deleted = NOW()
                WHERE id = #{blockId}
                    AND property_id = #{propertyId}
                    AND date_deleted IS NULL
            """)
    void cancelBlock(@Param("propertyId") UUID propertyId, @Param("blockId") UUID blockId);
}
