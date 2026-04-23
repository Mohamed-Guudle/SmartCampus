package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.service.SensorService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource class for managing Sensor entities.
 * Provides CRUD operations for sensors and supports filtering by type.
 * Also implements a sub-resource locator for accessing sensor readings.
 *
 * Base path: /api/v1/sensors
 *
 * Student: Mohamed Guudle (W2045871)
 */
@Path("/sensors")
public class SensorResource {

    // Service instance for sensor operations
    private final SensorService sensorService;

    // Default constructor - JAX-RS will instantiate this
    public SensorResource() {
        this.sensorService = SensorService.getInstance();
    }

    /**
     * Retrieves a list of all sensors, optionally filtered by type.
     * The type filter is passed as a query parameter (e.g., ?type=CO2).
     *
     * GET /api/v1/sensors
     * GET /api/v1/sensors?type=CO2
     *
     * @param type optional sensor type filter
     * @return JSON array of sensors (filtered if type is provided)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        Collection<Sensor> sensors;

        // If a type query parameter is provided, filter by that type
        if (type != null && !type.trim().isEmpty()) {
            sensors = sensorService.getSensorsByType(type);
        } else {
            sensors = sensorService.getAllSensors();
        }

        return Response.ok(sensors).build();
    }

    /**
     * Creates a new sensor in the system.
     * Validates that the referenced room exists before registration.
     * Returns 201 Created with a Location header.
     *
     * POST /api/v1/sensors
     *
     * @param sensor the sensor data from the request body
     * @return 201 Created with location header, or error response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        // Basic validation
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Sensor ID is required and cannot be empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (sensor.getType() == null || sensor.getType().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Sensor type is required and cannot be empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE"); // Default status
        }

        // Check if sensor already exists
        if (sensorService.sensorExists(sensor.getId())) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Conflict");
            error.put("message", "A sensor with ID '" + sensor.getId() + "' already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        Sensor createdSensor = sensorService.createSensor(sensor);

        // Build the Location URI for the newly created resource
        URI locationUri = URI.create("/api/v1/sensors/" + createdSensor.getId());

        return Response.created(locationUri)
                .entity(createdSensor)
                .build();
    }

    /**
     * Retrieves detailed information about a specific sensor.
     *
     * GET /api/v1/sensors/{sensorId}
     *
     * @param sensorId the unique identifier of the sensor
     * @return the sensor details, or 404 if not found
     */
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorService.getSensorById(sensorId);

        if (sensor == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(sensor).build();
    }

    /**
     * Sub-resource locator for sensor readings.
     * Delegates handling of /sensors/{sensorId}/readings to a separate
     * SensorReadingResource instance.
     *
     * This pattern keeps the code modular and maintainable by separating
     * the concerns of sensor management from reading management.
     *
     * @param sensorId the sensor ID from the path
     * @return a new SensorReadingResource configured for the given sensor
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadings(@PathParam("sensorId") String sensorId) {
        // Return a new SensorReadingResource initialized with the sensor ID
        return new SensorReadingResource(sensorId);
    }
}
