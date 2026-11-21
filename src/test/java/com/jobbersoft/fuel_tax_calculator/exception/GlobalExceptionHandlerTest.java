
package com.jobbersoft.fuel_tax_calculator.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/v1/tax/fuel/calculate");
    }

    @Test
    void handleJurisdictionNotFound_shouldReturn404() {
        var response = handler.handleJurisdictionNotFound(
                new JurisdictionNotFoundException("Jurisdiction not found"),
                request
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Jurisdiction not found", response.getBody().message());
        assertEquals("/v1/tax/fuel/calculate", response.getBody().path());
        assertEquals(404, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleValidation_shouldReturn400WithFieldErrors() {
        BindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError("request", "shipmentId",
                        "Shipment ID is required")
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        var response = handler.handleValidation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Request validation failed", response.getBody().message());
        assertEquals(
                "Shipment ID is required",
                response.getBody().validationErrors().get("shipmentId")
        );
    }

    @Test
    void handleConstraintViolation_shouldReturn400() {
        var exception = new jakarta.validation.ConstraintViolationException(
                "Invalid parameter", java.util.Set.of()
        );

        var response = handler.handleConstraintViolation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid parameter", response.getBody().message());
        assertNull(response.getBody().validationErrors());
    }

    @Test
    void handleBadRequest_shouldReturn400() {
        var response = handler.handleBadRequest(
                new IllegalArgumentException("Invalid input"),
                request
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody().message());
    }

    @Test
    void handleGeneralException_shouldReturn500WithGenericMessage() {
        var response = handler.handleGeneralException(
                new RuntimeException("Database connection failed"),
                request
        );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );
        assertEquals(
                "An unexpected error occurred",
                response.getBody().message()
        );
        assertEquals(500, response.getBody().status());
    }
}
