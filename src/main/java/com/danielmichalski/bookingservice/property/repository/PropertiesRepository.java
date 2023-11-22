package com.danielmichalski.bookingservice.property.repository;

import java.util.UUID;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PropertiesRepository {

  @Select("SELECT EXISTS(SELECT 1 FROM properties WHERE id=#{propertyId})")
  boolean propertyExists(@Param("propertyId") UUID propertyId);

}
