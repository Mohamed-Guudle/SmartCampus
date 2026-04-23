package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Discovery endpoint for the Smart Campus API.
 * Provides API metadata, versioning info, contact details,
 * and hypermedia links to all primary resource collections.
 *
 * This follows the HATEOAS (Hypermedia as the Engine of Application State)
 * principle by allowing clients to discover API capabilities dynamically
 * rather than relying on static documentation.
 *
 * Base path: GET /api/v1
 *
 * Student: Mohamed Guudle (W2045871)
 */
@Path("/")
public class DiscoveryResource {

    /**
     * Returns API metadata and navigation links.
     *
     * @return JSON object with API information and resource links
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        Map<String, Object> apiInfo = new HashMap<>();

        // API metadata
        apiInfo.put("name", "Smart Campus API");
        apiInfo.put("description", "RESTful API for managing campus rooms, sensors, and sensor readings.");
        apiInfo.put("version", "1.0.0");
        apiInfo.put("status", "operational");

        // Contact and documentation
        Map<String, String> contact = new HashMap<>();
        contact.put("name", "Smart Campus Support");
        contact.put("email", "smartcampus@university.ac.uk");
        contact.put("documentation", "https://github.com/W2045871/smart-campus-api/blob/main/README.md");
        apiInfo.put("contact", contact);

        // Hypermedia links to resource collections (HATEOAS)
        Map<String, String> links = new HashMap<>();
        links.put("self", "/api/v1/");
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        apiInfo.put("_links", links);

        // Resource descriptions
        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "Manage campus rooms (GET, POST, DELETE)");
        resources.put("sensors", "Manage sensors and filter by type (GET, POST)");
        resources.put("readings", "Access via /sensors/{sensorId}/readings (GET, POST)");
        apiInfo.put("resources", resources);

        return Response.ok(apiInfo).build();
    }
}
