package com.smartcampus.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception mapper that acts as a safety net for all unhandled exceptions.
 * Catches anything throwable that is not handled by a more specific exception mapper
 * and returns a generic HTTP 500 Internal Server Error response.
 *
 * IMPORTANT: This mapper deliberately avoids exposing any internal details
 * such as stack traces, class names, or internal error messages. This is a
 * critical security measure to prevent information leakage to potential attackers.
 *
 * Student: Mohamed Guudle (W2045871)
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // Log the actual error server-side for debugging (in a real app, use proper logging)
        System.err.println("[GlobalExceptionMapper] Unhandled exception: " + exception.getClass().getName());
        System.err.println("[GlobalExceptionMapper] Message: " + exception.getMessage());

        // Build a generic error response - NO stack traces, NO internal details
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("statusCode", 500);
        errorResponse.put("message", "An unexpected error occurred. Please try again later or contact support.");
        errorResponse.put("support", "smartcampus@university.ac.uk");
        errorResponse.put("timestamp", System.currentTimeMillis());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
