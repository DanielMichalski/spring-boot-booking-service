package com.danielmichalski.bookingservice.property.repository;

import com.danielmichalski.bookingservice.property.entity.PropertyBookingEntity;
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
public interface PropertyBookingsRepository {

    @Select("""
                SELECT id, guest_first_name, guest_last_name, date_created, start_date, end_date, property_id
                FROM property_bookings
                WHERE id = #{bookingId}
                    AND date_deleted IS NULL
            """)
    Optional<PropertyBookingEntity> findById(@Param("bookingId") UUID bookingId);

    @Select("""
                SELECT EXISTS(
                    SELECT 1 FROM property_bookings
                    WHERE property_id = #{propertyId}
                        AND date_deleted IS NULL
                        AND (
                            #{startDate} BETWEEN start_date AND end_date
                            OR #{endDate} BETWEEN start_date AND end_date
                            OR start_date BETWEEN #{startDate} AND #{endDate}
                        )
                )
            """)
    boolean bookingExistsWithinRange(@Param("propertyId") UUID propertyId,
                                     @Param("startDate") OffsetDateTime startDate,
                                     @Param("endDate") OffsetDateTime endDate);

    @Insert("""
                INSERT INTO property_bookings (id, guest_first_name, guest_last_name, date_created, start_date, end_date, property_id)
                VALUES (#{id}, #{guestFirstName}, #{guestLastName}, #{dateCreated}, #{startDate}, #{endDate}, #{propertyId})
            """)
    void bookProperty(PropertyBookingEntity propertyBookingEntity);

    @Update("""
                UPDATE property_bookings
                SET guest_first_name = #{guestFirstName},
                    guest_last_name = #{guestLastName},
                    start_date = #{startDate},
                    end_date = #{endDate},
                    date_updated = NOW()
                WHERE id = #{id}
                    AND date_deleted IS NULL
            """)
    void updateBooking(PropertyBookingEntity propertyBookingEntity);

    @Update("""
                UPDATE property_bookings
                SET date_deleted = NOW()
                WHERE id = #{bookingId}
                    AND property_id = #{propertyId}
                    AND date_deleted IS NULL
            """)
    void cancelBooking(@Param("propertyId") UUID propertyId, @Param("bookingId") UUID bookingId);

}
