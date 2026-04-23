package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception mapper for RoomNotEmptyException.
 * Converts the exception into an HTTP 409 Conflict response with a
 * structured JSON error body explaining that the room is occupied
 * by active hardware.
 *
 * HTTP 409 Conflict is used because the request conflicts with the
 * current state of the server - the room still has sensors and
 * cannot be deleted.
 *
 * Student: Mohamed Guudle (W2045871)
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        // Build a structured JSON error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Conflict");
        errorResponse.put("statusCode", 409);
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("roomId", exception.getRoomId());
        errorResponse.put("activeSensors", exception.getSensorCount());
        errorResponse.put("resolution", "Remove all sensors from this room before attempting deletion.");

        return Response.status(Response.Status.CONFLICT)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
