package com.modsen.rideservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.rideservice.dto.response.ExceptionResponse;
import com.modsen.rideservice.exception.CommunicationNotFoundException;
import com.modsen.rideservice.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();
    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionResponse message;
        try (InputStream bodyIs = response.body()
                .asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionResponse.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        return switch (response.status()) {
            case 400 -> new BadRequestException(message.getMessage() != null ? message.getMessage() : "Bad Request");
            case 404 -> new NotFoundException(message.getMessage() != null ? message.getMessage() : "Not found");
            default -> errorDecoder.decode(methodKey, response);
        };
    }
}
