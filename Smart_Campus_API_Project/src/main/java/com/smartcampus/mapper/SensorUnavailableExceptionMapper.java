package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception mapper for SensorUnavailableException.
 * Converts the exception into an HTTP 403 Forbidden response with a
 * structured JSON error body.
 *
 * HTTP 403 Forbidden is used (rather than 404) because the sensor
 * resource itself exists and was found. The server is refusing to
 * allow the action due to the sensor's current status, not because
 * the resource is missing.
 *
 * Student: Mohamed Guudle (W2045871)
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        // Build a structured JSON error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Forbidden");
        errorResponse.put("statusCode", 403);
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("sensorId", exception.getSensorId());
        errorResponse.put("currentStatus", exception.getCurrentStatus());
        errorResponse.put("resolution", "Wait for the sensor to return to ACTIVE status before submitting readings.");

        return Response.status(Response.Status.FORBIDDEN)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
