package com.smartcampus.resource;

import com.smartcampus.model.SensorReading;
import com.smartcampus.service.SensorReadingService;
import com.smartcampus.service.SensorService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource class for managing SensorReading entities for a specific sensor.
 * This is a sub-resource that is instantiated by the sub-resource locator
 * in SensorResource.
 *
 * Base path: /api/v1/sensors/{sensorId}/readings
 *
 * The sub-resource locator pattern delegates reading-related operations
 * to this dedicated class, keeping the code clean and modular.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class SensorReadingResource {

    // The sensor ID this resource instance is bound to
    private final String sensorId;

    // Service for reading operations
    private final SensorReadingService readingService;

    /**
     * Constructs a SensorReadingResource bound to a specific sensor.
     * This constructor is called by the sub-resource locator in SensorResource.
     *
     * @param sensorId the ID of the sensor whose readings we manage
     */
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
        this.readingService = SensorReadingService.getInstance();
    }

    /**
     * Retrieves all historical readings for this sensor.
     *
     * GET /api/v1/sensors/{sensorId}/readings
     *
     * @return JSON array of sensor readings
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        // Verify the sensor exists
        if (!SensorService.getInstance().sensorExists(sensorId)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        List<SensorReading> readings = readingService.getReadingsForSensor(sensorId);
        return Response.ok(readings).build();
    }

    /**
     * Creates a new reading for this sensor.
     * Side effect: updates the parent sensor's currentValue field.
     *
     * Returns 403 Forbidden if the sensor is in MAINTENANCE or OFFLINE status.
     *
     * POST /api/v1/sensors/{sensorId}/readings
     *
     * @param reading the reading data from the request body
     * @return 201 Created with the new reading
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReading(SensorReading reading) {
        // Verify the sensor exists
        if (!SensorService.getInstance().sensorExists(sensorId)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // Validate the reading value
        if (reading == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Reading data is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        SensorReading createdReading = readingService.createReading(sensorId, reading);

        return Response.status(Response.Status.CREATED)
                .entity(createdReading)
                .build();
    }
}
