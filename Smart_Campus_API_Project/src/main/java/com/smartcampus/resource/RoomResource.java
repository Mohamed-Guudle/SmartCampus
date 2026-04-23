package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.service.RoomService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource class for managing Room entities.
 * Provides CRUD operations for campus rooms.
 *
 * Base path: /api/v1/rooms
 *
 * Student: Mohamed Guudle (W2045871)
 */
@Path("/rooms")
public class RoomResource {

    // Service instance for room operations
    private final RoomService roomService;

    // Default constructor - JAX-RS will instantiate this
    public RoomResource() {
        this.roomService = RoomService.getInstance();
    }

    /**
     * Retrieves a list of all rooms in the system.
     * Returns full room objects including their sensor IDs.
     *
     * GET /api/v1/rooms
     *
     * @return JSON array of all rooms
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        Collection<Room> rooms = roomService.getAllRooms();
        return Response.ok(rooms).build();
    }

    /**
     * Creates a new room in the system.
     * Returns 201 Created with a Location header pointing to the new resource.
     *
     * POST /api/v1/rooms
     *
     * @param room the room data from the request body
     * @return 201 Created with location header, or 400 if data is invalid
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        // Basic validation
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Room ID is required and cannot be empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (room.getName() == null || room.getName().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Room name is required and cannot be empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (room.getCapacity() <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Room capacity must be a positive number.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        // Check if room already exists
        if (roomService.roomExists(room.getId())) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Conflict");
            error.put("message", "A room with ID '" + room.getId() + "' already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        Room createdRoom = roomService.createRoom(room);

        // Build the Location URI for the newly created resource
        URI locationUri = URI.create("/api/v1/rooms/" + createdRoom.getId());

        return Response.created(locationUri)
                .entity(createdRoom)
                .build();
    }

    /**
     * Retrieves detailed information about a specific room.
     *
     * GET /api/v1/rooms/{roomId}
     *
     * @param roomId the unique identifier of the room
     * @return the room details, or 404 if not found
     */
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = roomService.getRoomById(roomId);

        if (room == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Not Found");
            error.put("message", "Room with ID '" + roomId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(room).build();
    }

    /**
     * Deletes a room from the system.
     * This is handled by RoomService which throws RoomNotEmptyException (409).
     *
     * DELETE /api/v1/rooms/{roomId}
     *
     * @param roomId
     * @return 204
     */
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        roomService.deleteRoom(roomId);

        // Return 204 No Content on successful deletion
        // If the room didn't exist, we also return 204
        return Response.noContent().build();
    }
}
