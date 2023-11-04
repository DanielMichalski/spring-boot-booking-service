package com.danielmichalski.bookingservice.config.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.danielmichalski.bookingservice")
public class MyBatisConfig {

}
