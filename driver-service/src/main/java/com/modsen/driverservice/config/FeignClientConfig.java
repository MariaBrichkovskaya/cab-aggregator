package com.modsen.driverservice.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new PassengerErrorDecoder();
    }
    @Bean
    public Retryer retryer() {
        return new PassengerRetryer();
    }
}
