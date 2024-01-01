package com.modsen.rideservice.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new RideErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new RideRetryer();
    }
}
