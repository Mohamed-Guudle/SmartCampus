# Smart Campus API

**Student:** Mohamed Guudle  
**Student ID:** W2045871  
**Module:** 5COSC022W Client-Server Architectures  
**Coursework:** Smart Campus REST API (60% of final grade)

---

## Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Build and Run Instructions](#build-and-run-instructions)
5. [Sample cURL Commands](#sample-curl-commands)
6. [Report Answers](#report-answers)
   - [Part 1: Service Architecture & Setup](#part-1-service-architecture--setup)
   - [Part 2: Room Management](#part-2-room-management)
   - [Part 3: Sensor Operations & Linking](#part-3-sensor-operations--linking)
   - [Part 4: Deep Nesting with Sub-Resources](#part-4-deep-nesting-with-sub-resources)
   - [Part 5: Advanced Error Handling, Exception Mapping & Logging](#part-5-advanced-error-handling-exception-mapping--logging)

---

## Overview

The Smart Campus API is a RESTful web service built using **JAX-RS (Jersey)** that provides comprehensive management of campus rooms, sensors, and sensor readings. The API follows REST architectural principles including proper use of HTTP methods and status codes, HATEOAS for discoverability, and robust error handling with custom exceptions and exception mappers.

### Key Features

- **Discovery Endpoint** - API metadata and hypermedia navigation links
- **Room Management** - Create, list, retrieve, and delete rooms with safety constraints
- **Sensor Operations** - Register sensors with room validation, filter by type
- **Sub-Resource Pattern** - Nested readings under sensors via `/sensors/{id}/readings`
- **Advanced Error Handling** - Custom exceptions (409, 422, 403) with structured JSON error responses
- **Global Safety Net** - Catch-all mapper preventing stack trace leakage
- **Request/Response Logging** - JAX-RS filters for API observability
- **Thread-Safe Storage** - ConcurrentHashMap for concurrent access safety

### API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/` | API discovery/metadata |
| GET | `/api/v1/rooms` | List all rooms |
| POST | `/api/v1/rooms` | Create a new room |
| GET | `/api/v1/rooms/{roomId}` | Get room details |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room (409 if has sensors) |
| GET | `/api/v1/sensors` | List all sensors (optional `?type=` filter) |
| POST | `/api/v1/sensors` | Register a new sensor (validates roomId) |
| GET | `/api/v1/sensors/{sensorId}` | Get sensor details |
| GET | `/api/v1/sensors/{sensorId}/readings` | Get sensor reading history |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add a reading (403 if MAINTENANCE) |

---

## Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 11 | Programming language |
| JAX-RS (javax.ws.rs) | 2.1.1 | REST API specification |
| Jersey | 2.39.1 | JAX-RS implementation |
| Jackson | 2.x | JSON serialization/deserialization |
| Apache Tomcat | 9.0.100 | Embedded servlet container |
| Maven | 3.x | Build tool |

---

## Project Structure

```
smart-campus-api/
├── pom.xml                               # Maven build configuration
├── README.md                             # This file
└── src/
    └── main/
        └── java/com/smartcampus/
            ├── Main.java                  # Server bootstrap (Tomcat 9.0.100)
            ├── config/
            │   └── ApplicationConfig.java # JAX-RS application config
            ├── model/
            │   ├── Room.java              # Room entity
            │   ├── Sensor.java            # Sensor entity
            │   └── SensorReading.java     # SensorReading entity
            ├── resource/
            │   ├── DiscoveryResource.java # API discovery endpoint
            │   ├── RoomResource.java      # Room CRUD operations
            │   ├── SensorResource.java    # Sensor operations + sub-resource locator
            │   └── SensorReadingResource.java # Reading operations (sub-resource)
            ├── service/
            │   ├── RoomService.java       # Room business logic
            │   ├── SensorService.java     # Sensor business logic
            │   └── SensorReadingService.java # Reading business logic
            ├── exception/
            │   ├── RoomNotEmptyException.java              # 409 conflict
            │   ├── LinkedResourceNotFoundException.java    # 422 validation
            │   └── SensorUnavailableException.java         # 403 forbidden
            ├── mapper/
            │   ├── RoomNotEmptyExceptionMapper.java        # Maps to 409
            │   ├── LinkedResourceNotFoundExceptionMapper.java # Maps to 422
            │   ├── SensorUnavailableExceptionMapper.java   # Maps to 403
            │   └── GlobalExceptionMapper.java              # Maps to 500
            └── filter/
                └── LoggingFilter.java     # Request/response logging
```

---

## Build and Run Instructions

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Apache Maven 3.6+ installed and configured
- (Optional) cURL for testing
- (Optional) Postman for API exploration

### Step 1: Clone the Repository

```bash
git clone https://github.com/W2045871/smart-campus-api.git
cd smart-campus-api
```

### Step 2: Build the Project

```bash
mvn clean package
```

This command compiles the source code, runs tests, and creates a fat JAR file with all dependencies in the `target/` directory.

### Step 3: Run the Server

```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
```

The server will start on port 8080. You should see:

```
=================================================
  Smart Campus API Server Started!
  Embedded Server: Apache Tomcat 9.0.100
  Listening on: http://localhost:8080/api/v1
=================================================
```

### Step 4: (Optional) Custom Port

To run on a different port, pass the port number as an argument:

```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar 9090
```

### Step 5: Verify the Server

Open your browser or use cURL to test the discovery endpoint:

```bash
curl http://localhost:8080/api/v1/
```

You should receive a JSON response with API metadata and navigation links.

### Stopping the Server

Press `Ctrl+C` in the terminal where the server is running.

---

## Sample cURL Commands

### 1. Discovery Endpoint - Get API Metadata

```bash
curl -X GET http://localhost:8080/api/v1/ \
  -H "Accept: application/json"
```

**Expected Response (200 OK):**
```json
{
  "name": "Smart Campus API",
  "description": "RESTful API for managing campus rooms, sensors, and sensor readings.",
  "version": "1.0.0",
  "status": "operational",
  "contact": {
    "name": "Smart Campus Support",
    "email": "smartcampus@university.ac.uk",
    "documentation": "https://github.com/W2045871/smart-campus-api/blob/main/README.md"
  },
  "_links": {
    "self": "/api/v1/",
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  },
  "resources": {
    "rooms": "Manage campus rooms (GET, POST, DELETE)",
    "sensors": "Manage sensors and filter by type (GET, POST)",
    "readings": "Access via /sensors/{sensorId}/readings (GET, POST)"
  }
}
```

---

### 2. Create a New Room

```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "id": "CONF-201",
    "name": "Conference Room 201",
    "capacity": 15
  }'
```

**Expected Response (201 Created):**
```json
{
  "id": "CONF-201",
  "name": "Conference Room 201",
  "capacity": 15,
  "sensorIds": []
}
```

The response includes a `Location` header pointing to `/api/v1/rooms/CONF-201`.

---

### 3. List All Sensors with Type Filtering

**List all sensors:**
```bash
curl -X GET http://localhost:8080/api/v1/sensors \
  -H "Accept: application/json"
```

**Filter by CO2 sensors only:**
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2" \
  -H "Accept: application/json"
```

**Expected Response (200 OK):**
```json
[
  {
    "id": "CO2-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 450.0,
    "roomId": "LIB-301"
  }
]
```

---

### 4. Add a Sensor Reading via Sub-Resource

```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "value": 23.8
  }'
```

**Expected Response (201 Created):**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "timestamp": 1713801600000,
  "value": 23.8
}
```

After this request, the parent sensor's `currentValue` field will be updated to `23.8`.

---

### 5. Attempt to Delete a Room with Sensors (409 Conflict)

```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301 \
  -H "Accept: application/json"
```

**Expected Response (409 Conflict):**
```json
{
  "error": "Conflict",
  "statusCode": 409,
  "message": "Room 'LIB-301' cannot be deleted because it still has 2 sensor(s) assigned to it. Please remove all sensors before deleting the room.",
  "roomId": "LIB-301",
  "activeSensors": 2,
  "resolution": "Remove all sensors from this room before attempting deletion."
}
```

---

### 6. Attempt to Create Sensor with Invalid Room (422 Unprocessable Entity)

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "id": "TEMP-999",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 20.0,
    "roomId": "NON-EXISTENT-ROOM"
  }'
```

**Expected Response (422):**
```json
{
  "error": "Unprocessable Entity",
  "statusCode": 422,
  "message": "The Room 'NON-EXISTENT-ROOM' referenced in field 'roomId' does not exist in the system.",
  "field": "roomId",
  "invalidValue": "NON-EXISTENT-ROOM",
  "resourceType": "Room",
  "resolution": "Ensure the referenced resource exists before creating this resource."
}
```

---

### 7. Attempt Reading on Maintenance Sensor (403 Forbidden)

```bash
curl -X POST http://localhost:8080/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "value": 5.0
  }'
```

**Expected Response (403 Forbidden):**
```json
{
  "error": "Forbidden",
  "statusCode": 403,
  "message": "Sensor 'OCC-001' is currently in 'MAINTENANCE' status and cannot accept new readings. Please wait until the sensor returns to ACTIVE status.",
  "sensorId": "OCC-001",
  "currentStatus": "MAINTENANCE",
  "resolution": "Wait for the sensor to return to ACTIVE status before submitting readings."
}
```

---

## Report Answers

The following sections provide detailed answers to all questions specified in the coursework.

---

### Part 1: Service Architecture & Setup

#### Question 1.1: JAX-RS Resource Class Lifecycle and Thread Safety

> Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures to prevent data loss or race conditions.

**Answer:**

By default, JAX-RS resource classes are **request-scoped**, meaning the runtime instantiates a new instance of the resource class for every incoming HTTP request. This is the standard behavior defined by the JAX-RS specification. Each request gets its own fresh instance of the resource, which is then garbage-collected after the response is sent. This design was chosen by the JAX-RS specification authors to promote thread safety at the resource level, since each request operates on its own object instance and cannot interfere with other concurrent requests through instance variables.

However, this default request-scoped lifecycle has significant implications for how we manage application state, especially when using in-memory data structures like `HashMap` or `ArrayList`. Since each request creates a new resource instance, any instance fields in the resource class would be reset for every request. This means we cannot store application data as instance variables within the resource class itself, because that data would be lost as soon as the request completes.

To solve this, my implementation uses **singleton service classes** (`RoomService`, `SensorService`, `SensorReadingService`) that are shared across all requests. Each service class follows the Singleton design pattern with a private constructor and a static `getInstance()` method. The resource classes obtain a reference to these singleton services, either through dependency injection or direct instantiation in the constructor. This ensures that all requests operate on the same underlying data store.

Because multiple requests can now access the shared data simultaneously, thread synchronization becomes critical. My implementation uses `ConcurrentHashMap` instead of a regular `HashMap` for the primary data stores. `ConcurrentHashMap` provides thread-safe operations by dividing the map into segments and locking only the relevant segment during write operations, rather than locking the entire map. This provides much better concurrency performance than using `synchronized` blocks or `Collections.synchronizedMap()`, which would lock the entire data structure for every read and write.

For operations that involve multiple steps (such as adding a sensor and then updating the room's sensor list), the individual map operations are atomic, but the combined operation is not strictly transactional. In a production environment, we would use additional locking mechanisms or database transactions. However, for this coursework's scope, `ConcurrentHashMap` provides sufficient protection against the most common race conditions, such as two threads simultaneously writing to the same map entry.

Additionally, the service methods are designed to be stateless where possible. They accept all necessary parameters and return results without maintaining request-specific state. This functional approach minimizes the risk of data corruption and makes the code easier to reason about in a concurrent environment.

---

#### Question 1.2: HATEOAS Benefits

> Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

**Answer:**

HATEOAS (Hypermedia as the Engine of Application State) is considered a hallmark of advanced RESTful design because it represents the **maturity level 3** of the Richardson Maturity Model, which is the highest level of REST adherence. At this level, the API not only uses proper HTTP verbs and resources but also provides hypermedia links that allow clients to navigate the API dynamically, much like how humans navigate websites by following links.

My discovery endpoint at `GET /api/v1/` demonstrates this principle by returning not just data, but also `_links` that point to the primary resource collections:

```json
{
  "_links": {
    "self": "/api/v1/",
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

This approach benefits client developers in several significant ways:

**1. Loose Coupling:** Clients don't need to hardcode URLs into their applications. They can discover available resources at runtime by following the links provided in responses. If the API changes its URL structure in a future version, existing clients will continue to work as long as they follow the links rather than constructing URLs manually.

**2. Self-Documenting API:** The responses themselves tell the client what actions are available at any given point. For example, when a client retrieves a room, the response could (in a more advanced implementation) include links to related sensors, making it immediately obvious what operations are possible next. This reduces the learning curve for new developers.

**3. Reduced Dependency on External Documentation:** While static documentation (like Swagger/OpenAPI specs or README files) is still valuable, it can become outdated. HATEOAS ensures that the API's current capabilities are always reflected in the actual responses. Clients can adapt to API changes dynamically without requiring code updates.

**4. State Machine Navigation:** HATEOAS effectively turns the API into a state machine where each response contains the valid state transitions. For example, a room resource might include a "delete" link only when it has no sensors, guiding the client through valid workflows and preventing invalid operations.

**5. Improved Discoverability:** New developers can explore the API by starting at the root endpoint and following links, just like browsing a website. This makes the API more intuitive and reduces the time needed to understand its structure and capabilities.

Compared to static documentation, which is a separate artifact that must be maintained and can drift out of sync with the actual implementation, HATEOAS embeds the API's contract within the responses themselves, making it always up-to-date and self-descriptive.

---

### Part 2: Room Management

#### Question 2.1: ID-Only vs Full Object Returns

> When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.

**Answer:**

This design decision involves a fundamental trade-off between **payload size** and **client convenience**, and both approaches have valid use cases depending on the scenario.

**Returning Full Objects (my implementation's approach):**

My `GET /api/v1/rooms` endpoint returns the complete room objects including `id`, `name`, `capacity`, and `sensorIds`. This approach has several advantages:

- **Reduced Number of Requests:** The client gets all the data it needs in a single request. If we only returned IDs, the client would need to make additional requests to `GET /api/v1/rooms/{id}` for each room to get basic information like the room name. This "N+1 query problem" at the API level would significantly increase server load and latency.
- **Better User Experience:** Frontend applications typically need at least the room name and capacity to display a list view. Returning full objects allows the UI to render immediately without cascading requests.
- **Simpler Client Code:** The client doesn't need to implement complex orchestration logic to fetch and merge related data.

The disadvantage is **larger payload size**. If rooms have many fields or large nested objects, the response body grows accordingly, consuming more network bandwidth. However, for my implementation where rooms have only a few simple fields, the payload remains small and manageable.

**Returning Only IDs:**

An ID-only approach would return something like `["LIB-301", "LAB-102", "LEC-HALL-A"]`. This minimizes the initial payload to its absolute minimum, which is beneficial when:

- The client only needs a count or existence check
- The network is extremely constrained (e.g., mobile networks with limited data)
- The resource objects are very large with many fields

However, the client then bears the burden of making follow-up requests for any actual data, which introduces latency and complexity.

**Best Practice - Hybrid Approach:**

In production APIs, a common compromise is to support **projection** or **field selection** via query parameters (e.g., `?fields=id,name`), allowing the client to specify exactly which fields it needs. This gives the client control over the payload size while maintaining convenience. Another approach is to return a **summary representation** in list endpoints and the **full representation** in detail endpoints, which is the pattern my API follows (`GET /rooms` returns summaries, `GET /rooms/{id}` returns full details including sensor relationships).

For this coursework, returning full objects in the list endpoint is appropriate because the room entities are small, and it provides the best developer experience for API consumers.

---

#### Question 2.2: DELETE Operation Idempotency

> Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

**Answer:**

**Yes, my DELETE implementation is idempotent.**

Idempotency is a fundamental property of HTTP methods where making the same request multiple times produces the same result as making it once. This is particularly important for DELETE because network issues can cause clients to retry requests, and we need to ensure that retrying a deletion doesn't cause errors or unexpected side effects.

In my implementation, the `deleteRoom(String id)` method in `RoomService` handles the deletion as follows:

1. **First DELETE request:** The method looks up the room by ID. If the room exists and has no sensors assigned, it is removed from the `ConcurrentHashMap`, and the method returns successfully. The resource returns **204 No Content**, indicating successful deletion.

2. **Second (or subsequent) DELETE request for the same ID:** The method again looks up the room by ID. Since the room was already deleted, `rooms.get(id)` returns `null`. My implementation treats this as a success and returns **204 No Content** without throwing an error.

This behavior ensures idempotency because:
- The server state after the first DELETE (room does not exist) is identical to the server state after any subsequent DELETE.
- The client receives the same response status (204 No Content) every time.
- No error is thrown for attempting to delete a non-existent resource.

However, there is a **deliberate exception** to this idempotent behavior: if the room still has sensors assigned, the first DELETE request will fail with a **409 Conflict** (via `RoomNotEmptyException`), and this same 409 response will be returned for every subsequent retry until the sensors are removed. This is still considered idempotent in the HTTP specification sense because the server state doesn't change - the room remains undeleted - and the client receives the same response each time.

This design follows RFC 7231, which states that DELETE "ought to" be idempotent, and a 404 (Not Found) response is acceptable if the resource was already deleted. My implementation uses 204 instead of 404 for the "already deleted" case, which is a common and client-friendly approach that avoids making clients distinguish between "successfully deleted now" and "was already deleted before."

---

### Part 3: Sensor Operations & Linking

#### Question 3.1: @Consumes Mismatch Consequences

> We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

**Answer:**

The `@Consumes` annotation in JAX-RS defines the **media types** that a resource method can accept in the request body. When I annotate my `POST /sensors` method with `@Consumes(MediaType.APPLICATION_JSON)`, I am explicitly telling the JAX-RS runtime that this method only accepts requests with `Content-Type: application/json`.

**Technical Consequences of a Mismatch:**

If a client sends a request with a different `Content-Type` (e.g., `text/plain` or `application/xml`), the JAX-RS runtime will:

1. **Reject the request before it reaches the resource method.** The JAX-RS framework performs content negotiation during the request routing phase. It examines the `Content-Type` header and matches it against the `@Consumes` annotation of candidate resource methods.

2. **Return HTTP 415 Unsupported Media Type.** This is the standard HTTP status code indicating that the server refuses to accept the request because the payload format is not supported. The request is blocked at the framework level, and my resource method code never executes.

**How JAX-RS Handles the Mismatch:**

JAX-RS uses a **content negotiation algorithm** during request dispatching:

1. The client sends a request with `Content-Type: text/plain`
2. JAX-RS examines all resource methods matching the requested path and HTTP method
3. It filters out methods whose `@Consumes` annotation doesn't include `text/plain`
4. If no matching method is found, it returns 415 Unsupported Media Type

This is a **security and robustness feature** because:
- It prevents the server from attempting to parse data in an unexpected format, which could lead to parsing errors or security vulnerabilities.
- It provides clear feedback to the client about what formats are accepted.
- It happens automatically at the framework level without requiring manual validation in every resource method.

**Example Scenario:**

If a client sends:
```
POST /api/v1/sensors
Content-Type: text/plain

id=TEMP-001&type=Temperature
```

The JAX-RS runtime will respond with:
```
HTTP/1.1 415 Unsupported Media Type
```

To make the API work, the client must use:
```
POST /api/v1/sensors
Content-Type: application/json

{"id": "TEMP-001", "type": "Temperature", ...}
```

**Complementary @Produces Annotation:**

Similarly, my methods use `@Produces(MediaType.APPLICATION_JSON)` to indicate that responses will be in JSON format. If a client sends `Accept: application/xml`, JAX-RS would return **406 Not Acceptable**, indicating the requested response format is not available.

Together, `@Consumes` and `@Produces` create a clear contract between the client and server about data formats, which is essential for interoperability in RESTful systems.

---

#### Question 3.2: QueryParam vs PathParam for Filtering

> You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

**Answer:**

My implementation uses `@QueryParam` for filtering sensors by type: `GET /api/v1/sensors?type=CO2`. An alternative design would use a path parameter: `GET /api/v1/sensors/type/CO2`. While both approaches can work, the query parameter approach is generally considered superior for filtering and searching, and here's why:

**1. RESTful Semantics - Path Represents Resource Hierarchy, Query Represents Parameters:**

In REST design, the URL path should represent the **resource hierarchy** and **identity**. `/api/v1/sensors` represents the collection of all sensors. Adding `/type/CO2` to the path implies that "CO2" is a sub-resource or a nested collection under "type," which is semantically misleading because "type" is not a resource - it's a filter criterion.

Query parameters, on the other hand, are designed for **modifiers** - they specify how the collection should be processed, sorted, or filtered without changing the fundamental identity of the resource being accessed. `?type=CO2` clearly signals "give me a filtered view of the sensors collection" rather than "navigate to a different resource."

**2. Composability and Multiple Filters:**

Query parameters naturally support multiple simultaneous filters. For example:
```
GET /api/v1/sensors?type=CO2&status=ACTIVE
GET /api/v1/sensors?type=Temperature&roomId=LIB-301&status=ACTIVE
```

With path parameters, combining multiple filters becomes awkward:
```
GET /api/v1/sensors/type/CO2/status/ACTIVE
```

This creates a rigid URL structure where the order of parameters matters and adding new filters requires changing the path template.

**3. Optional Filtering:**

Query parameters are inherently optional. `GET /api/v1/sensors` without any query parameters returns all sensors, while `GET /api/v1/sensors?type=CO2` returns a filtered subset. Both requests hit the same resource method, and the framework handles the optional parameter gracefully.

With path parameters, you typically need separate resource methods or complex path templates to handle both filtered and unfiltered cases:
```
@GET
@Path("/sensors")                    // All sensors
@Path("/sensors/type/{type}")        // Filtered by type
```

This leads to code duplication or more complex routing logic.

**4. Empty or Special Characters:**

Query parameters handle empty values and special characters more naturally. For example, filtering by a type with spaces:
```
GET /api/v1/sensors?type=Air%20Quality
```

With path parameters, spaces and special characters can cause routing issues or require additional encoding/decoding logic.

**5. Caching Considerations:**

HTTP caching mechanisms (like intermediary proxies) treat URLs with different query strings as separate cache entries by default. While this can be configured, it actually works in our favor for filtering because `GET /sensors?type=CO2` and `GET /sensors?type=Temperature` should indeed be cached separately as they represent different result sets.

**When Path Parameters ARE Appropriate:**

Path parameters are better when the parameter is part of the resource's identity. For example, `GET /api/v1/sensors/TEMP-001` uses a path parameter because we're accessing a specific, identifiable resource. The sensor ID is not a filter - it's the identity of the resource we want.

In summary, query parameters are the industry-standard approach for collection filtering because they maintain clean RESTful semantics, support composable filters, handle optional parameters gracefully, and result in more maintainable code.

---

### Part 4: Deep Nesting with Sub-Resources

#### Question 4.1: Sub-Resource Locator Pattern Benefits

> Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

**Answer:**

The **Sub-Resource Locator pattern** is a powerful JAX-RS feature that promotes clean architecture by delegating the handling of nested resources to dedicated resource classes. In my implementation, the `SensorResource` class contains a sub-resource locator method:

```java
@Path("/{sensorId}/readings")
public SensorReadingResource getSensorReadings(@PathParam("sensorId") String sensorId) {
    return new SensorReadingResource(sensorId);
}
```

This method returns a new instance of `SensorReadingResource`, which then handles all requests for `/sensors/{sensorId}/readings` and its sub-paths. This architectural pattern provides several significant benefits:

**1. Separation of Concerns:**

Each resource class is responsible for exactly one type of resource. `SensorResource` handles sensor-level operations (creating sensors, listing sensors, getting sensor details), while `SensorReadingResource` handles reading-level operations (getting history, adding new readings). This separation makes the codebase easier to understand, test, and maintain. When a developer needs to modify reading-related logic, they know exactly which class to look at.

**2. Reduced Class Complexity:**

Without the sub-resource locator pattern, all endpoints for both sensors and readings would need to be defined in a single `SensorResource` class. For a path like `sensors/{id}/readings/{rid}`, the parent class would need methods annotated with `@Path("/{id}/readings/{rid}")`. As the API grows and more nested resources are added (e.g., `/sensors/{id}/readings/{rid}/alerts`, `/sensors/{id}/config`), the parent class becomes enormous and difficult to navigate.

**3. Encapsulation of Resource State:**

The sub-resource class can encapsulate state that is specific to the nested resource context. In my implementation, `SensorReadingResource` stores the `sensorId` as an instance variable, which is set during construction by the sub-resource locator. This means the sub-resource methods don't need to repeatedly extract the sensor ID from path parameters - it's already available as part of the object's state. This makes the code cleaner and reduces duplication.

**4. Independent Testing:**

Sub-resource classes can be unit tested independently of their parent resource. I can test `SensorReadingResource`'s `getReadings()` and `createReading()` methods without needing to instantiate or configure `SensorResource`. This improves test isolation and makes the test suite more maintainable.

**5. Reusability and Composition:**

A sub-resource class could potentially be returned by multiple different parent resources if the nested resource pattern repeats elsewhere in the API. While not applicable in this specific coursework, this flexibility is valuable in larger systems where similar nested relationships might exist under different parent resources.

**6. Cleaner URL Routing:**

The sub-resource locator makes the routing structure mirror the URL hierarchy. `SensorResource` handles `/sensors`, and when it sees `/sensors/{id}/readings`, it delegates to `SensorReadingResource` which handles everything under that prefix. This hierarchical delegation is intuitive and matches how developers think about REST URL structures.

**Comparison to a "Massive Controller" Approach:**

In a monolithic design, a single controller might have 15-20 methods handling sensors, readings, alerts, configurations, and more. Finding the right method to modify becomes a challenge, and the class likely accumulates many dependencies (services, mappers, validators) that aren't all needed for every operation. The Sub-Resource Locator pattern prevents this "god class" anti-pattern by distributing functionality across focused, cohesive classes that each have a single, well-defined responsibility.

---

### Part 5: Advanced Error Handling, Exception Mapping & Logging

#### Question 5.2: Semantic Accuracy of 422 vs 404

> Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

**Answer:**

HTTP **422 Unprocessable Entity** is more semantically accurate than **404 Not Found** for the scenario in my implementation (POSTing a sensor with a non-existent `roomId`) because the two status codes describe fundamentally different categories of errors, and understanding this distinction is important for building precise, self-documenting APIs.

**The Semantic Difference:**

- **404 Not Found** means "the resource you are trying to access does not exist." In the context of `POST /api/v1/sensors`, a 404 would imply that the `/sensors` endpoint itself doesn't exist or that the URL is wrong. This is a **routing-level** error - the server cannot find the endpoint or resource that matches the request URL.

- **422 Unprocessable Entity** means "the server understands the request and the syntax is correct, but the semantic content is invalid." This is a **payload-level** error - the URL is correct, the JSON syntax is valid, but the data inside the JSON refers to something that doesn't exist. The RFC 4918 specification (WebDAV) defines 422 as appropriate when the server "understands the content type of the request entity, and the syntax of the request entity is correct, but was unable to process the contained instructions."

**Why 422 is Better in This Case:**

1. **The Request Reached the Right Endpoint:** When a client POSTs to `/api/v1/sensors` with `{"roomId": "NON-EXISTENT"}`, the request successfully reached the sensor creation endpoint. The JAX-RS framework parsed the JSON, populated the `Sensor` object, and called the `createSensor()` method. A 404 would incorrectly suggest that the client used the wrong URL.

2. **The JSON Syntax is Valid:** The request body is well-formed JSON. If the JSON were malformed (e.g., missing a closing brace), JAX-RS would return 400 Bad Request before reaching the resource method. Since the JSON is valid but contains a semantic error (a bad foreign key), 422 precisely describes the situation.

3. **Clearer Client Guidance:** A 422 response with a detailed error body (as my implementation provides) tells the client exactly what went wrong: "The Room 'NON-EXISTENT-ROOM' referenced in field 'roomId' does not exist." This allows the client to take corrective action - in this case, either create the room first or correct the roomId. A 404 would leave the client confused about whether the URL was wrong or the room was missing.

4. **Consistency with API Design Patterns:** Major APIs (GitHub, Stripe, AWS) use 422 for validation errors where the request format is correct but the data is invalid. This has become an industry convention that developers expect.

**My Implementation's Approach:**

My `LinkedResourceNotFoundExceptionMapper` returns 422 with a structured error response that includes the field name, invalid value, and resource type. This gives the client all the information needed to diagnose and fix the issue:

```json
{
  "error": "Unprocessable Entity",
  "statusCode": 422,
  "message": "The Room 'NON-EXISTENT-ROOM' referenced in field 'roomId' does not exist in the system.",
  "field": "roomId",
  "invalidValue": "NON-EXISTENT-ROOM",
  "resourceType": "Room"
}
```

In summary, 404 is about the URL path, while 422 is about the request payload. Since the problem is a missing reference inside valid JSON, 422 is the semantically precise choice.

---

#### Question 5.4: Security Risks of Stack Trace Exposure

> From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:**

Exposing Java stack traces in API error responses is a **critical security vulnerability** that violates the principle of **security through obscurity** and enables multiple attack vectors. My `GlobalExceptionMapper` explicitly prevents this by returning only a generic error message with no internal details.

**Information Disclosure Risks:**

A typical Java stack trace exposed in an HTTP response might look like this:

```
java.lang.NullPointerException
    at com.smartcampus.service.SensorService.createSensor(SensorService.java:87)
    at com.smartcampus.resource.SensorResource.createSensor(SensorResource.java:45)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at org.glassfish.jersey.server.model.internal.ResourceMethodInvocationHandlerFactory$1.invoke(...)
    at org.glassfish.jersey.server.ApplicationHandler.handle(ApplicationHandler.java:1150)
```

An attacker can extract the following **actionable intelligence** from this trace:

**1. Internal Package and Class Structure:**
The stack trace reveals the full package hierarchy (`com.smartcampus.service`, `com.smartcampus.resource`) and exact class names (`SensorService`, `SensorResource`). This gives attackers a blueprint of the application's internal architecture, which they can use to craft targeted attacks.

**2. Method Names and Line Numbers:**
Attackers can see exactly which methods are being called and where (`createSensor` at line 87). This helps them:
- Identify business logic vulnerabilities by understanding what operations the application performs
- Correlate code structure with known vulnerabilities in specific libraries
- Focus their testing on specific methods that handle sensitive operations

**3. Technology Stack Fingerprinting:**
The trace reveals that the application uses:
- **Jersey** (`org.glassfish.jersey`) - the JAX-RS implementation
- **Sun/Oracle JVM** (`sun.reflect`) - the Java runtime vendor

With this information, an attacker can:
- Look up known CVEs (Common Vulnerabilities and Exposures) for Jersey 2.x
- Research version-specific exploits for the identified frameworks
- Determine if the application is running on an outdated Java version with known security flaws

**4. Framework and Library Versions:**
Stack traces from framework internals often include version numbers in package paths or JAR filenames. An attacker might see `jersey-server-2.39.1.jar` in a detailed error, allowing them to look up specific vulnerabilities for that exact version.

**5. Database and External System Details:**
If the stack trace includes a `SQLException` or connection error, it might reveal:
- Database type (PostgreSQL, MySQL, Oracle)
- Database server hostnames or IP addresses
- Connection string details
- SQL query structure (enabling SQL injection refinement)

**Attack Scenarios Enabled by Stack Trace Exposure:**

- **Path Traversal:** Knowing the internal package structure helps attackers craft requests to access unintended endpoints or resources.
- **Framework-Specific Exploits:** If the attacker knows you're using Jersey 2.39.1, they can look up and weaponize known deserialization vulnerabilities or injection flaws specific to that version.
- **Denial of Service:** Understanding the internal method flow allows attackers to craft requests that trigger expensive operations or resource exhaustion.
- **Social Engineering:** Detailed technical information about the stack makes phishing attacks more convincing, as attackers can reference specific internal technologies.

**Defense in Depth:**

My `GlobalExceptionMapper` implements the defense strategy of **failing securely**:

```java
// Log the error internally (for debugging)
System.err.println("[GlobalExceptionMapper] Unhandled exception: " + exception.getClass().getName());

// Return generic response to client (no internal details)
errorResponse.put("error", "Internal Server Error");
errorResponse.put("message", "An unexpected error occurred. Please try again later or contact support.");
```

The actual error is logged server-side for developer debugging, but the external response contains only a generic message. This follows the principle that APIs should be **helpful to legitimate users while revealing nothing to potential attackers**.

---

#### Question 5.5: Advantages of JAX-RS Filters Over Manual Logging

> Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

**Answer:**

Using JAX-RS filters for cross-cutting concerns like logging provides significant architectural advantages over manual logging statements in every resource method. My implementation uses a single `LoggingFilter` class that implements both `ContainerRequestFilter` and `ContainerResponseFilter`, and it automatically intercepts all requests and responses without any changes to the resource classes.

**1. DRY Principle - Don't Repeat Yourself:**

With manual logging, every resource method would need duplicate logging code:

```java
@GET
public Response getAllRooms() {
    LOGGER.info("GET /api/v1/rooms");  // Duplicated in every method
    // ... business logic
    LOGGER.info("Response: 200");       // Duplicated in every method
    return Response.ok(rooms).build();
}

@POST
public Response createRoom(Room room) {
    LOGGER.info("POST /api/v1/rooms");  // Same pattern, different method
    // ... business logic
    LOGGER.info("Response: 201");
    return Response.created(uri).build();
}
```

If the logging format needs to change (e.g., adding a timestamp or request ID), every single method must be updated. With filters, the logging logic exists in exactly one place, and changes apply globally.

**2. Consistency and Reliability:**

Manual logging is prone to human error. Developers might:
- Forget to add logging to new methods
- Use inconsistent log formats across different methods
- Miss logging the response status if an exception occurs before the log statement
- Copy-paste logging code but forget to update the method name

Filters guarantee that **every request is logged identically**, regardless of which resource method handles it. The framework invokes the filter for every request, so no method can be accidentally missed.

**3. Separation of Concerns:**

Logging is a **cross-cutting concern** - it applies to the entire application but is not part of the core business logic. Mixing logging code with business logic violates the Single Responsibility Principle. Resource methods should focus on handling requests and responses, not on infrastructure concerns like logging.

Filters keep the resource classes **clean and focused** on their primary responsibility:

```java
// Clean resource method - only business logic
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response getAllRooms() {
    Collection<Room> rooms = roomService.getAllRooms();
    return Response.ok(rooms).build();
}
```

**4. Access to Framework Context:**

JAX-RS filters receive `ContainerRequestContext` and `ContainerResponseContext` objects, which provide rich information about the request and response that might not be easily accessible within the resource method:

```java
// In the filter, we have access to:
requestContext.getMethod();           // HTTP method
requestContext.getUriInfo().getRequestUri();  // Full request URI
requestContext.getHeaders();          // All request headers
responseContext.getStatus();          // Response status code
responseContext.getHeaders();         // All response headers
```

Resource methods don't typically have direct access to the response status code they will return (it's set when building the Response object), but response filters can capture it after the method completes.

**5. Ordering and Chaining:**

JAX-RS supports multiple filters with defined priority ordering via the `@Priority` annotation. This allows sophisticated processing pipelines:

```java
@Priority(1)  // Runs first - Authentication
public class AuthFilter implements ContainerRequestFilter { ... }

@Priority(2)  // Runs second - Logging
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter { ... }

@Priority(3)  // Runs third - Rate limiting
public class RateLimitFilter implements ContainerRequestFilter { ... }
```

This declarative ordering is much cleaner than trying to sequence manual logging calls within resource methods.

**6. Reusability Across Applications:**

A well-designed filter can be packaged as a library and reused across multiple JAX-RS applications. My `LoggingFilter` could be extracted into a shared library and dropped into any Jersey project without code changes. Manual logging code would need to be copied and adapted for each project.

**7. Non-Invasive Enhancement:**

Filters can be added, removed, or modified without touching any resource code. This is valuable for:
- Adding temporary debug logging during troubleshooting
- Removing logging in production for performance
- Swapping logging implementations (e.g., switching from `java.util.logging` to Log4j)

In summary, JAX-RS filters provide a **declarative, centralized, and maintainable** approach to cross-cutting concerns that is superior to the **imperative, scattered, and error-prone** approach of manual logging statements.

---

## Conclusion

This Smart Campus API demonstrates core RESTful principles including proper HTTP method usage, appropriate status codes, HATEOAS discoverability, robust error handling with custom exceptions, thread-safe in-memory storage, and cross-cutting concern management via JAX-RS filters. The implementation follows industry best practices and provides a solid foundation for a scalable campus sensor management system.
