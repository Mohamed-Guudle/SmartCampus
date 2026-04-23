package com.smartcampus.service;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service class for managing Room entities.
 * Uses a ConcurrentHashMap for thread-safe in-memory storage.
 *
 * All operations are synchronized at the map level to prevent race conditions
 * when multiple requests modify room data concurrently.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class RoomService {

    // Thread-safe map storing rooms keyed by their ID
    private final ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();

    // Singleton instance (safe because JAX-RS creates one instance per app)
    private static final RoomService INSTANCE = new RoomService();

    private RoomService() {
        // Private constructor to enforce singleton pattern
        // Pre-populate with some sample data for demonstration
        initializeSampleData();
    }

    public static RoomService getInstance() {
        return INSTANCE;
    }

    /**
     * Pre-populates the system with sample rooms for testing.
     */
    private void initializeSampleData() {
        Room room1 = new Room("LIB-301", "Library Quiet Study", 30);
        Room room2 = new Room("LAB-102", "Computer Science Lab", 25);
        Room room3 = new Room("LEC-HALL-A", "Main Lecture Hall", 200);

        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);
        rooms.put(room3.getId(), room3);
    }

    /**
     * Retrieves all rooms in the system.
     *
     * @return collection of all rooms
     */
    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    /**
     * Retrieves a specific room by its ID.
     *
     * @param id the room ID
     * @return the Room object, or null if not found
     */
    public Room getRoomById(String id) {
        return rooms.get(id);
    }

    /**
     * Checks if a room with the given ID exists.
     *
     * @param id the room ID to check
     * @return true if the room exists
     */
    public boolean roomExists(String id) {
        return rooms.containsKey(id);
    }

    /**
     * Creates a new room in the system.
     *
     * @param room the room to create
     * @return the created room
     */
    public Room createRoom(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    /**
     * Deletes a room from the system.
     * Business rule: a room cannot be deleted if it still has sensors assigned.
     *
     * @param id the ID of the room to delete
     * @throws RoomNotEmptyException if the room still has sensors
     */
    public void deleteRoom(String id) {
        Room room = rooms.get(id);
        if (room == null) {
            return; // Room doesn't exist, treat as success (idempotency)
        }

        // Check if the room has sensors - prevent orphan data
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(id, room.getSensorIds().size());
        }

        rooms.remove(id);
    }

    /**
     * Adds a sensor reference to a room.
     *
     * @param roomId   the room ID
     * @param sensorId the sensor ID to add
     */
    public void addSensorToRoom(String roomId, String sensorId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.addSensorId(sensorId);
        }
    }

    /**
     * Removes a sensor reference from a room.
     *
     * @param roomId   the room ID
     * @param sensorId the sensor ID to remove
     */
    public void removeSensorFromRoom(String roomId, String sensorId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.removeSensorId(sensorId);
        }
    }
}
