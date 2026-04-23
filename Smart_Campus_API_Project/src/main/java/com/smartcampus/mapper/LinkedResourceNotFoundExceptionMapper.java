package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception mapper for LinkedResourceNotFoundException.
 * Converts the exception into an HTTP 422 Unprocessable Entity response
 * with a structured JSON error body.
 *
 * HTTP 422 is used because the request was well-formed and understood,
 * but the payload contains a semantic error - a reference to a resource
 * that does not exist. This is more precise than 400 Bad Request
 * because the JSON syntax is valid; the issue is with the content.
 *
 * Student: Mohamed Guudle (W2045871)
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // Build a structured JSON error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unprocessable Entity");
        errorResponse.put("statusCode", 422);
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("field", exception.getFieldName());
        errorResponse.put("invalidValue", exception.getInvalidValue());
        errorResponse.put("resourceType", exception.getResourceType());
        errorResponse.put("resolution", "Ensure the referenced resource exists before creating this resource.");

        return Response.status(422)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
