# Smart Campus API

**Student:** Mohamed Guudle  
**Student ID:** W2045871  
**Module:** 5COSC022W Client-Server Architectures  


---

## Overview

The Smart Campus API is a RESTful web service built using **JAX-RS (Jersey)** that provides comprehensive management of campus rooms, sensors, and sensor readings. The application is bootstrapped on an **embedded Apache Tomcat 9.0.100** servlet container, replacing the Jetty server used in earlier iterations. The API follows REST architectural principles including proper use of HTTP methods and status codes, HATEOAS for discoverability, and robust error handling with custom exceptions and exception mappers.

### Key Features

- **Discovery Endpoint** — API metadata and hypermedia navigation links (HATEOAS)
- **Room Management** — Create, list, retrieve, and delete rooms with safety constraints
- **Sensor Operations** — Register sensors with room validation, filter by type via `@QueryParam`
- **Sub-Resource Locator Pattern** — Nested readings under sensors via `/sensors/{id}/readings`
- **Advanced Error Handling** — Custom exceptions (409, 422, 403) with structured JSON error responses
- **Global Safety Net** — Catch-all exception mapper preventing stack trace leakage (500)
- **Request/Response Logging** — JAX-RS `ContainerRequestFilter` / `ContainerResponseFilter` for observability
- **Thread-Safe Storage** — `ConcurrentHashMap` for concurrent access safety

### API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/` | API discovery / metadata |
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
| Java | 11+ | Programming language |
| JAX-RS (`javax.ws.rs`) | 2.1.1 | REST API specification |
| Jersey | 2.39.1 | JAX-RS reference implementation |
| Jackson | 2.x (via Jersey) | JSON serialisation / deserialisation |
| **Apache Tomcat (Embedded)** | **9.0.100** | **Embedded servlet container** |
| Maven | 3.6+ | Build tool and dependency management |



---

## Project Structure

```
smart-campus-api/
├── pom.xml                               # Maven build configuration (Tomcat + Jersey)
├── README.md                             # This file
└── src/
    └── main/
        └── java/com/smartcampus/
            ├── Main.java                  # Server bootstrap (Embedded Tomcat 9.0.100)
            ├── config/
            │   └── ApplicationConfig.java # JAX-RS Application class
            ├── model/
            │   ├── Room.java              # Room entity
            │   ├── Sensor.java            # Sensor entity (holds readings list)
            │   └── SensorReading.java     # SensorReading entity
            ├── resource/
            │   ├── DiscoveryResource.java # GET /api/v1/ — HATEOAS root
            │   ├── RoomResource.java      # Room CRUD operations
            │   ├── SensorResource.java    # Sensor operations + sub-resource locator
            │   └── SensorReadingResource.java # Reading operations (sub-resource)
            ├── service/
            │   ├── RoomService.java       # Room business logic (singleton)
            │   ├── SensorService.java     # Sensor business logic (singleton)
            │   └── SensorReadingService.java # Reading business logic (singleton)
            ├── exception/
            │   ├── RoomNotEmptyException.java              # → 409 Conflict
            │   ├── LinkedResourceNotFoundException.java    # → 422 Unprocessable Entity
            │   └── SensorUnavailableException.java         # → 403 Forbidden
            ├── mapper/
            │   ├── RoomNotEmptyExceptionMapper.java        # Maps → 409
            │   ├── LinkedResourceNotFoundExceptionMapper.java # Maps → 422
            │   ├── SensorUnavailableExceptionMapper.java   # Maps → 403
            │   └── GlobalExceptionMapper.java              # Catch-all → 500
            └── filter/
                └── LoggingFilter.java     # Request / response logging filter
```

### How the Pieces Fit Together

```
Client Request
      │
      ▼
┌──────────────────────────┐
│  Embedded Tomcat 9.0.100 │   ← Main.java bootstraps here
│  (Servlet Container)     │
└────────────┬─────────────┘
             │  ServletContainer (jersey-container-servlet)
             ▼
┌──────────────────────────┐
│  LoggingFilter            │   ← @Provider: logs every request/response
├──────────────────────────┤
│  Jersey JAX-RS Runtime    │   ← ApplicationConfig registers classes
├──────────────────────────┤
│  Resource Classes         │   ← Route to the correct @Path method
│  (Room / Sensor / …)     │
├──────────────────────────┤
│  Service Singletons       │   ← Business logic + ConcurrentHashMap
├──────────────────────────┤
│  Exception Mappers        │   ← Convert exceptions → structured JSON
└──────────────────────────┘
```

---

## Build and Run Instructions

### Prerequisites

| Requirement | Notes |
|-------------|-------|
| **JDK 11 or higher** | `java -version` to verify |
| **Apache Maven 3.6+** | `mvn -version` to verify |
| **NetBeans IDE** (optional) | 12.x or newer recommended |
| **cURL or Postman** (optional) | For API testing |

> **No separate Tomcat installation is required.** The embedded Tomcat 9.0.100 libraries are pulled automatically by Maven.

### Step 1 — Open the Project

#### Option A: NetBeans IDE (Recommended)

1. **File → Open Project** and navigate to the `smart-campus-api` folder.
2. NetBeans will recognise the `pom.xml` and import it as a Maven project.
3. Wait for the IDE to finish resolving dependencies (watch the bottom status bar).

#### Option B: Command Line

```bash
cd smart-campus-api
```

### Step 2 — Build the Project

#### NetBeans

Right-click the project → **Clean and Build** (or press `Shift+F11`).

#### Command Line

```bash
mvn clean package
```

Maven will:
1. Compile all source files.
2. Shade every dependency into a single **fat JAR** (via `maven-shade-plugin`).
3. Produce `target/smart-campus-api-1.0-SNAPSHOT.jar`.

### Step 3 — Run the Server

#### NetBeans

Right-click `Main.java` → **Run File** (or press `Shift+F6`).

#### Command Line

```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
```

You will see the following banner in the console:

```
=================================================
  Smart Campus API Server Started!
  Powered by: Apache Tomcat 9.0.100
  Listening on: http://localhost:8080/api/v1
=================================================
```

### Step 4 — (Optional) Custom Port

Pass the port number as a command-line argument:

```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar 9090
```

In NetBeans: **Run → Set Project Configuration → Customise…** → add `9090` to the arguments field.

### Step 5 — Verify the Server

```bash
curl http://localhost:8080/api/v1/
```

You should receive a JSON response with API metadata and navigation links.

### Stopping the Server

Press `Ctrl+C` in the terminal, or click the red **Stop** button in NetBeans.

---

## Sample cURL Commands

Below are **seven** representative commands that exercise every major feature of the API.

### 1. Discovery Endpoint — Get API Metadata

```bash
curl -s -X GET http://localhost:8080/api/v1/ \
  -H "Accept: application/json" | python -m json.tool
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

### 2. Create a New Room (POST)

```bash
curl -s -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "id": "CONF-201",
    "name": "Conference Room 201",
    "capacity": 15
  }' | python -m json.tool
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

A `Location: /api/v1/rooms/CONF-201` header is also returned.

---

### 3. List All Rooms (GET)

```bash
curl -s -X GET http://localhost:8080/api/v1/rooms \
  -H "Accept: application/json" | python -m json.tool
```

**Expected Response (200 OK):** A JSON array containing the three pre-populated rooms (`LIB-301`, `LAB-102`, `LEC-HALL-A`) plus any you have created.

---

### 4. Filter Sensors by Type (GET with QueryParam)

```bash
curl -s -X GET "http://localhost:8080/api/v1/sensors?type=CO2" \
  -H "Accept: application/json" | python -m json.tool
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

### 5. Add a Sensor Reading via the Sub-Resource (POST)

```bash
curl -s -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{ "value": 23.8 }' | python -m json.tool
```

**Expected Response (201 Created):**

```json
{
    "id": "a1b2c3d4-...-ef1234567890",
    "timestamp": 1713801600000,
    "value": 23.8
}
```

**Side-effect:** the parent sensor `TEMP-001`'s `currentValue` is updated to `23.8`.

---

### 6. Delete a Room That Still Has Sensors — 409 Conflict

```bash
curl -s -X DELETE http://localhost:8080/api/v1/rooms/LIB-301 \
  -H "Accept: application/json" | python -m json.tool
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

### 7. Create a Sensor Referencing a Non-Existent Room — 422 Unprocessable Entity

```bash
curl -s -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "id": "TEMP-999",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 20.0,
    "roomId": "NON-EXISTENT-ROOM"
  }' | python -m json.tool
```

**Expected Response (422 Unprocessable Entity):**

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

### Bonus: Submit a Reading to a MAINTENANCE Sensor — 403 Forbidden

```bash
curl -s -X POST http://localhost:8080/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{ "value": 5.0 }' | python -m json.tool
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

The following sections provide detailed answers to every question specified in the coursework brief.

---

### Part 1: Service Architecture & Setup

---

#### Question 1.1 — JAX-RS Resource Class Lifecycle and Thread Safety

> *Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures to prevent data loss or race conditions.*

**Answer:**

By default, JAX-RS resource classes are **request-scoped**. This means the JAX-RS runtime (Jersey, in our case) creates a **brand-new instance** of the resource class for every incoming HTTP request. Once the response has been sent, that instance becomes eligible for garbage collection. This is the behaviour specified in the JAX-RS 2.1 specification (JSR 370) and it is the default unless you explicitly annotate the class with `@Singleton`.

This design has a deliberate rationale: because every request gets its own object, there is no possibility of two concurrent requests corrupting each other's **instance fields**. Each thread operates on its own copy of the resource object. However, this default lifecycle creates two immediate challenges for in-memory state management:

1. **Data Persistence Problem:** Any data stored in instance variables of a resource class is lost the moment the request ends. If `RoomResource` stored a `HashMap<String, Room>` as an instance field, every request would start with an empty map.

2. **Shared-State Problem:** To make data survive across requests, we must store it somewhere that outlives individual resource instances. In my implementation, I use **singleton service classes** (`RoomService`, `SensorService`, `SensorReadingService`), each of which is eagerly initialised via the classic `private static final INSTANCE` pattern:

   ```java
   private static final RoomService INSTANCE = new RoomService();

   private RoomService() {
       initializeSampleData();
   }

   public static RoomService getInstance() {
       return INSTANCE;
   }
   ```

   Every request-scoped resource object calls `RoomService.getInstance()` in its constructor, ensuring all requests share the same backing data.

Because multiple threads may invoke service methods concurrently (Tomcat's thread pool dispatches requests in parallel), **thread-safety** becomes critical. My implementation addresses this with `ConcurrentHashMap`:

```java
private final ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();
```

`ConcurrentHashMap` provides the following guarantees:

- **Atomic single-key operations:** `get()`, `put()`, `remove()`, and `containsKey()` are individually thread-safe without explicit synchronisation.
- **Segment-level locking (Java 8+: node-level CAS):** Unlike `Collections.synchronizedMap()`, which locks the entire map on every access, `ConcurrentHashMap` uses a fine-grained locking strategy that allows multiple reads and writes to proceed concurrently on different keys.
- **No `ConcurrentModificationException`:** Iterators are weakly consistent, meaning they never throw `ConcurrentModificationException` even when the map is modified during iteration.

For compound operations such as "check if room has sensors, then delete it", the individual `get()` and `remove()` calls are each atomic, but the two-step sequence is not. In a production system we would use `computeIfPresent()` or explicit locks. For this coursework's demonstration scope, the protection provided by `ConcurrentHashMap` is sufficient and appropriate.

**In summary:** JAX-RS creates a new resource per request (request-scoped). I store shared state in singleton services backed by `ConcurrentHashMap`, keeping the resource classes stateless and the data stores thread-safe.

---

#### Question 1.2 — HATEOAS Benefits

> *Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?*

**Answer:**

HATEOAS (Hypermedia as the Engine of Application State) is the defining constraint that elevates a web API from **Level 2** to **Level 3** on the Richardson Maturity Model. At Level 3, the server's responses contain not just data but also **hypermedia controls** — links and actions — that tell the client what it can do next. My discovery endpoint at `GET /api/v1/` demonstrates this:

```json
{
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

This approach benefits client developers in several important ways:

**1. Loose Coupling between Client and Server**

When clients hard-code URLs such as `http://api.example.com/v1/rooms`, they are tightly coupled to the server's URL structure. If the server team renames the path to `/v2/campus-rooms`, every client breaks. With HATEOAS, clients start at the root endpoint, follow the `rooms` link, and never construct a URL themselves. The server can reorganise its URI space without breaking clients — only the link targets change.

**2. Self-Describing Responses**

Each response tells the client its available next steps. In a more advanced implementation, a room response might include:

```json
{
  "id": "LIB-301",
  "_links": {
    "self": "/api/v1/rooms/LIB-301",
    "sensors": "/api/v1/rooms/LIB-301/sensors",
    "delete": "/api/v1/rooms/LIB-301"
  }
}
```

The client does not need to memorise the URL pattern for sensors within a room; the server provides the link at runtime. This turns the API into a **navigable graph**, much like following hyperlinks on a web page.

**3. Reduced Reliance on External Documentation**

Static documentation (Swagger pages, README files, Postman collections) is a snapshot taken at a point in time. It can drift out of sync with the live implementation. HATEOAS embeds the API contract **inside the responses themselves**, so it is always up-to-date. A developer can explore the entire API by starting at the root and clicking links in a tool like Postman — no documentation lookup required.

**4. State-Machine Guidance**

HATEOAS can conditionally include or exclude links based on the current state of a resource. For example, a room with no sensors could include a `"delete"` link, while a room with sensors omits it. This signals to the client that deletion is not currently permitted, reducing invalid requests and improving the user experience.

**5. Discoverability and Onboarding**

New developers can start with a single URL (`/api/v1/`) and discover the entire API surface by following links, just like browsing a website. This dramatically lowers the onboarding curve compared to reading a lengthy specification document.

**Compared to static documentation**, HATEOAS is not a replacement but a complement. Documentation explains *why* and *how*; HATEOAS shows *what is available right now*. Together they provide a robust developer experience, but if forced to choose, the self-describing nature of HATEOAS is more resilient to change.

---

### Part 2: Room Management

---

#### Question 2.1 — ID-Only vs Full Object Returns

> *When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.*

**Answer:**

The choice between returning bare IDs and returning full objects is a classic **bandwidth-vs-round-trips** trade-off.

**My implementation returns full objects:**

```json
[
  { "id": "LIB-301", "name": "Library Quiet Study", "capacity": 30, "sensorIds": ["TEMP-001", "CO2-001"] },
  { "id": "LAB-102", "name": "Computer Science Lab", "capacity": 25, "sensorIds": ["OCC-001"] }
]
```

**Advantages of returning full objects:**

| Benefit | Explanation |
|---------|-------------|
| **Fewer round-trips** | The client gets everything in one request. Returning only IDs forces the client to issue N additional `GET /rooms/{id}` calls — the classic **N+1 problem** at the HTTP level. On high-latency networks this is devastating. |
| **Immediate rendering** | A front-end can render a room list immediately; it doesn't need to wait for follow-up fetches to know each room's name or capacity. |
| **Simpler client code** | No orchestration logic is required to merge partial results. |
| **Better cacheability** | A single cached response serves the whole list view. |

**Advantages of returning only IDs:**

| Benefit | Explanation |
|---------|-------------|
| **Minimal payload** | `["LIB-301","LAB-102","LEC-HALL-A"]` is a few bytes. If each room had dozens of fields or large nested objects, the difference would be significant. |
| **Lower server-side cost** | The server does not need to serialise full objects. |
| **Useful for existence checks** | If the client only needs to know *which* rooms exist, IDs suffice. |

**Best-practice compromise — field selection / projections:**

Production APIs often support a `?fields=id,name` query parameter so clients can request exactly the fields they need. This gives clients control over the payload without forcing extra round-trips. My implementation returns full objects because Room entities are small (four fields), so the bandwidth overhead is negligible and the developer experience is maximised.

---

#### Question 2.2 — DELETE Operation Idempotency

> *Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.*

**Answer:**

**Yes, my DELETE implementation is idempotent.**

Idempotency means that the effect of performing the operation N times is the same as performing it once. The relevant RFC (RFC 7231 §4.2.2) states that the methods GET, HEAD, PUT, and DELETE are defined as idempotent. Here is exactly what happens in my code when a DELETE is repeated:

**Scenario: `DELETE /api/v1/rooms/CONF-201` sent three times**

| Attempt | `RoomService.deleteRoom("CONF-201")` behaviour | HTTP response |
|---------|------------------------------------------------|---------------|
| 1st | `rooms.get("CONF-201")` returns the room. It has no sensors. `rooms.remove("CONF-201")` executes. | **204 No Content** |
| 2nd | `rooms.get("CONF-201")` returns `null`. The method returns immediately (`return;`). | **204 No Content** |
| 3rd | Same as 2nd. | **204 No Content** |

The critical code path in `RoomService.deleteRoom()`:

```java
Room room = rooms.get(id);
if (room == null) {
    return; // Room doesn't exist — treat as success (idempotency)
}
```

After the first successful deletion, the room no longer exists. Subsequent requests find `null` and return silently. The resource class then returns `204 No Content` every time. The server state (room absent) is identical after the first, second, and third calls — the definition of idempotent behaviour.

**Edge case — room with sensors (409 Conflict):**

If the room has sensors, `RoomNotEmptyException` is thrown every time, and the client always receives 409. The server state does not change (the room remains), so this is still idempotent — the same request always produces the same outcome.

**Why 204 instead of 404 on a re-DELETE?**

Some APIs return 404 when the resource has already been removed. My implementation returns 204 because:
- It is more client-friendly: the client's goal (ensure the room does not exist) is achieved regardless.
- It avoids forcing clients to distinguish "I deleted it" from "it was already gone".
- Both approaches are valid under RFC 7231; mine prioritises simplicity for the consumer.

---

### Part 3: Sensor Operations & Linking

---

#### Question 3.1 — @Consumes Mismatch Consequences

> *We explicitly use the `@Consumes(MediaType.APPLICATION_JSON)` annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as `text/plain` or `application/xml`. How does JAX-RS handle this mismatch?*

**Answer:**

The `@Consumes` annotation declares the **media types** a resource method is willing to accept. My sensor creation endpoint is annotated:

```java
@POST
@Consumes(MediaType.APPLICATION_JSON)   // only application/json
@Produces(MediaType.APPLICATION_JSON)
public Response createSensor(Sensor sensor) { ... }
```

**What happens when the `Content-Type` does not match:**

When a request arrives, the JAX-RS runtime executes the following content-negotiation algorithm (defined in JAX-RS §3.7.2):

1. **Match the URI and HTTP method** → finds the `POST /sensors` method.
2. **Check `Content-Type` against `@Consumes`** → if the request header says `text/plain` but the method only consumes `application/json`, there is no match.
3. **No matching method found** → the runtime returns **HTTP 415 Unsupported Media Type**.

This all happens **before** the resource method is ever invoked. Jersey never attempts to deserialise the body, and the `createSensor()` Java method never executes.

**Concrete example:**

```
POST /api/v1/sensors HTTP/1.1
Content-Type: text/plain

id=TEMP-001&type=Temperature
```

Response:

```
HTTP/1.1 415 Unsupported Media Type
```

**Complementary annotation — `@Produces`:**

Similarly, `@Produces(MediaType.APPLICATION_JSON)` declares the response format. If the client sends `Accept: application/xml` and no resource method produces XML, the runtime returns **HTTP 406 Not Acceptable**.

**Why this matters:**

| Benefit | Explanation |
|---------|-------------|
| **Security** | The server never attempts to parse unexpected formats, preventing parser-confusion attacks (e.g., XXE injection via XML). |
| **Correctness** | A `text/plain` body cannot be deserialised into a `Sensor` POJO. Catching the mismatch early avoids cryptic 500 errors. |
| **Clear contract** | The 415 status code tells the client *exactly* what is wrong and how to fix it (use `application/json`). |
| **No manual checks** | Without `@Consumes`, the developer would need to inspect the `Content-Type` header manually in every method. The annotation moves this responsibility to the framework. |

---

#### Question 3.2 — QueryParam vs PathParam for Filtering

> *You implemented this filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g., `/api/v1/sensors/type/CO2`). Why is the query parameter approach generally considered superior for filtering and searching collections?*

**Answer:**

My implementation uses:

```
GET /api/v1/sensors?type=CO2
```

The alternative path-based design would be:

```
GET /api/v1/sensors/type/CO2
```

The query-parameter approach is superior for filtering for the following reasons:

**1. Semantic Correctness — Paths Identify Resources, Queries Modify Views**

In REST, the URL path identifies **what** resource you are addressing. `/api/v1/sensors` identifies the sensor collection. Query parameters specify **how** you want the collection presented (filtered, sorted, paginated). Adding `/type/CO2` to the path implies that `CO2` is a sub-resource or a separate collection, which is semantically misleading — we are still looking at the same sensors collection, just a filtered view of it.

**2. Composability of Multiple Filters**

Query parameters compose naturally:

```
GET /api/v1/sensors?type=CO2&status=ACTIVE&roomId=LIB-301
```

With path parameters, combining filters becomes rigid and order-dependent:

```
GET /api/v1/sensors/type/CO2/status/ACTIVE/roomId/LIB-301
```

Adding a new filter dimension (e.g., `minValue`) requires a new path template, new route configuration, and careful ordering. Query parameters simply add another `&key=value`.

**3. Optionality**

Query parameters are inherently optional. Both of these are valid:

```
GET /api/v1/sensors          → all sensors
GET /api/v1/sensors?type=CO2 → filtered subset
```

Both hit the same `@GET` method. In my code the `@QueryParam("type")` parameter defaults to `null`, and the method checks for it:

```java
if (type != null && !type.trim().isEmpty()) {
    sensors = sensorService.getSensorsByType(type);
} else {
    sensors = sensorService.getAllSensors();
}
```

With path parameters, you would need **two** separate resource methods or complex regex path templates to handle the "no filter" and "with filter" cases.

**4. Caching and Proxy Friendliness**

HTTP caches (CDNs, reverse proxies) treat different query strings as distinct cache keys by default. `?type=CO2` and `?type=Temperature` are naturally separate cache entries, which is exactly what we want for different filter results.

**5. Industry Convention**

Major APIs (GitHub, Stripe, AWS, Google) universally use query parameters for collection filtering. Developers expect this pattern; deviating from it increases the learning curve.

**When PathParam IS correct:**

`@PathParam` is appropriate when the parameter is part of the resource's **identity**, not a filter. `GET /api/v1/sensors/TEMP-001` uses a path parameter because `TEMP-001` is the unique identifier of a specific sensor — it is not a search criterion.

---

### Part 4: Deep Nesting with Sub-Resources

---

#### Question 4.1 — Sub-Resource Locator Pattern Benefits

> *Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., `sensors/{id}/readings/{rid}`) in one massive controller class?*

**Answer:**

In my implementation, `SensorResource` contains a sub-resource locator method:

```java
@Path("/{sensorId}/readings")
public SensorReadingResource getSensorReadings(@PathParam("sensorId") String sensorId) {
    return new SensorReadingResource(sensorId);
}
```

This method has **no HTTP-method annotation** (`@GET`, `@POST`, etc.). Instead it returns a new instance of `SensorReadingResource`, and the JAX-RS runtime continues dispatching the request into that class. This is the Sub-Resource Locator pattern (JAX-RS §3.4.1), and it provides significant architectural benefits:

**1. Separation of Concerns**

Each resource class has a single, well-defined responsibility:

| Class | Responsibility |
|-------|---------------|
| `SensorResource` | Sensor CRUD + filtering |
| `SensorReadingResource` | Reading creation + history |

A developer modifying reading logic never needs to open `SensorResource`. This follows the **Single Responsibility Principle** and reduces merge conflicts in team environments.

**2. Avoiding the "God Class" Anti-Pattern**

Without sub-resource locators, every nested endpoint would live in `SensorResource`:

```java
@GET @Path("/{id}")                           // get sensor
@GET @Path("/{id}/readings")                  // get readings
@POST @Path("/{id}/readings")                 // add reading
@GET @Path("/{id}/readings/{rid}")            // get single reading
@DELETE @Path("/{id}/readings/{rid}")          // delete reading
@GET @Path("/{id}/config")                    // get sensor config
@PUT @Path("/{id}/config")                    // update config
@GET @Path("/{id}/alerts")                    // get alerts
```

As the API grows, this class balloons into 15–20 methods with many injected dependencies. The sub-resource locator pattern distributes these across focused classes, each of manageable size.

**3. Encapsulation of Parent Context**

The sub-resource instance stores the `sensorId` as a field:

```java
public class SensorReadingResource {
    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }
}
```

Every method in `SensorReadingResource` can use `this.sensorId` directly, avoiding the repetitive `@PathParam("sensorId") String sensorId` on every method signature.

**4. Independent Unit Testing**

`SensorReadingResource` can be tested in isolation by constructing it directly:

```java
SensorReadingResource resource = new SensorReadingResource("TEMP-001");
Response response = resource.getReadings();
// Assert on response...
```

No `SensorResource` setup, no server bootstrapping, no HTTP client needed.

**5. Reusability**

If a future requirement added readings to a different parent (e.g., `/api/v1/rooms/{roomId}/environment-readings`), the same `SensorReadingResource` class could be reused with minimal modification — the parent locator simply passes a different context.

**6. Clean URL-to-Code Mapping**

The URL hierarchy mirrors the class hierarchy:

```
/sensors              → SensorResource
/sensors/{id}/readings → SensorReadingResource (returned by locator)
```

Developers can immediately infer which class handles a given URL, which accelerates debugging and onboarding.

---

### Part 5: Advanced Error Handling, Exception Mapping & Logging

---

#### Question 5.1 — Semantic Accuracy of 422 vs 404

> *Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?*

**Answer:**

When a client sends `POST /api/v1/sensors` with `{"roomId": "NON-EXISTENT-ROOM"}`, the correct status is **422 Unprocessable Entity**, not 404 Not Found. The distinction is fundamental:

| Aspect | 404 Not Found | 422 Unprocessable Entity |
|--------|---------------|--------------------------|
| **What failed?** | The **URL** — the server cannot find the endpoint or resource addressed by the request URI. | The **payload** — the URL is correct, the JSON syntax is valid, but the data's *meaning* is invalid. |
| **Layer** | Routing / resource resolution | Business-logic validation |
| **Client action** | Fix the URL | Fix the data in the request body |

**Why 404 is wrong in this scenario:**

The request `POST /api/v1/sensors` **did** reach a valid endpoint. Jersey matched the path and method, parsed the JSON into a `Sensor` POJO, and invoked `createSensor()`. Everything succeeded up to the point where the service layer discovered that the referenced `roomId` does not exist. A 404 would tell the client "the URL `/api/v1/sensors` does not exist", which is false and confusing.

**Why 422 is right:**

RFC 4918 defines 422 as: *"The server understands the content type of the request entity (hence a 415 Unsupported Media Type status code is inappropriate), and the syntax of the request entity is correct (thus a 400 Bad Request status code is inappropriate), but was unable to process the contained instructions."*

This matches our case exactly:

1. Content type is `application/json` — understood ✓
2. JSON syntax is valid — parseable ✓
3. The *semantic content* (a foreign-key reference to a non-existent room) is invalid ✗

My `LinkedResourceNotFoundExceptionMapper` returns a rich 422 response:

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

This gives the client everything it needs: which field is wrong, what value was rejected, and what to do about it. Major APIs (GitHub, Stripe, Shopify) use 422 for this exact category of validation error, making it the de-facto industry standard.

---

#### Question 5.2 — Security Risks of Stack Trace Exposure

> *From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?*

**Answer:**

Exposing raw Java stack traces in HTTP responses is classified as **CWE-209: Generation of Error Message Containing Sensitive Information** by the MITRE Common Weakness Enumeration. My `GlobalExceptionMapper` explicitly prevents this by returning only a generic message:

```java
errorResponse.put("error", "Internal Server Error");
errorResponse.put("message", "An unexpected error occurred. Please try again later or contact support.");
```

Here is what an unprotected stack trace reveals and how each piece can be weaponised:

**1. Internal Package and Class Structure**

A trace like:
```
at com.smartcampus.service.SensorService.createSensor(SensorService.java:87)
at com.smartcampus.resource.SensorResource.createSensor(SensorResource.java:45)
```

…reveals the full package tree (`com.smartcampus.service`, `com.smartcampus.resource`), exact class names, method names, and line numbers. This gives an attacker a **blueprint** of the application's internal architecture, enabling targeted attacks against specific methods or known vulnerability patterns (e.g., a method named `executeQuery` signals SQL usage).

**2. Technology Stack Fingerprinting**

Framework-internal frames such as:
```
at org.glassfish.jersey.server.ApplicationHandler.handle(...)
at org.apache.catalina.core.ApplicationFilterChain.doFilter(...)
```

…reveal that the application uses Jersey 2.x on Tomcat 9.x. The attacker can then:
- Search the CVE database for known vulnerabilities in those exact versions.
- Look up deserialisation or injection exploits specific to Jersey or Tomcat.
- Determine the Java version from `sun.reflect.*` or `jdk.internal.*` frames.

**3. Library and Version Identification**

Stack traces from dependencies often include JAR filenames:
```
at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3482)
```

This tells the attacker the exact Jackson version, which has had several critical deserialisation vulnerabilities (CVE-2019-12384, CVE-2020-36518, etc.).

**4. Database and Infrastructure Leakage**

A `SQLException` trace might expose:
- Database vendor (`org.postgresql.jdbc.PgStatement`)
- Connection URLs (`jdbc:postgresql://db-prod.internal:5432/campus`)
- SQL fragments that reveal schema structure

**5. Attack Scenarios Enabled**

| Attack | How the trace helps |
|--------|-------------------|
| **Targeted exploit** | Knowing the exact Jersey + Tomcat version lets the attacker use a known CVE payload. |
| **Path traversal** | Knowing class and package names helps craft requests to hit unintended code paths. |
| **SQL injection refinement** | SQL fragments in traces reveal table and column names, letting the attacker refine injection payloads. |
| **Social engineering** | Referencing internal class names in a phishing email makes it more convincing to developers. |
| **Denial of service** | Understanding the call chain lets attackers craft inputs that trigger expensive code paths. |

**My Defence:**

The `GlobalExceptionMapper` logs the full exception server-side (for developer debugging) and returns a sanitised response to the client. This follows the OWASP principle: *"Be helpful to legitimate users while revealing nothing to potential attackers."*

---

#### Question 5.3 — Advantages of JAX-RS Filters Over Manual Logging

> *Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting `Logger.info()` statements inside every single resource method?*

**Answer:**

My `LoggingFilter` implements both `ContainerRequestFilter` and `ContainerResponseFilter`:

```java
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOGGER.info(String.format("[REQUEST] %s %s",
            requestContext.getMethod(),
            requestContext.getUriInfo().getRequestUri()));
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        LOGGER.info(String.format("[RESPONSE] %s %s -> Status: %d",
            requestContext.getMethod(),
            requestContext.getUriInfo().getRequestUri(),
            responseContext.getStatus()));
    }
}
```

This single class handles logging for **every** endpoint. Here is why this is superior to inserting `Logger.info()` calls in every resource method:

**1. DRY — Don't Repeat Yourself**

Without the filter, every resource method would contain duplicated logging boilerplate:

```java
@GET
public Response getAllRooms() {
    LOGGER.info("GET /api/v1/rooms");           // duplicated
    Collection<Room> rooms = roomService.getAllRooms();
    LOGGER.info("Response: 200");               // duplicated
    return Response.ok(rooms).build();
}
```

With 10 endpoints, that is 20 nearly identical log statements to maintain. If the log format changes (e.g., adding a correlation ID), every method must be updated. The filter centralises the logic in **one place**.

**2. Guaranteed Coverage**

Manual logging is human-dependent: a developer might forget to add it to a new endpoint, or might accidentally delete it during refactoring. Filters are registered globally via `@Provider` and are invoked by the framework for **every** request — it is impossible to miss an endpoint.

**3. Separation of Concerns (Single Responsibility Principle)**

Logging is a **cross-cutting concern** — it applies to all endpoints but is orthogonal to business logic. Resource methods should do one thing: handle the business request. Mixing logging, authentication, rate-limiting, and other infrastructure concerns into resource methods violates the Single Responsibility Principle and makes the code harder to read, test, and maintain.

With the filter, resource methods remain clean:

```java
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response getAllRooms() {
    return Response.ok(roomService.getAllRooms()).build();
}
```

**4. Access to Response Metadata**

A `ContainerResponseFilter` receives the `ContainerResponseContext` which contains the final status code, headers, and entity. Inside a resource method, the response status is not available until the `Response` object is built. If an exception mapper changes the status, the resource method's manual log would record the wrong code. The response filter always logs the **actual** status sent to the client.

**5. Ordering and Composability**

JAX-RS supports chaining multiple filters with explicit priority:

```java
@Priority(Priorities.AUTHENTICATION)  // runs first
public class AuthFilter implements ContainerRequestFilter { ... }

@Priority(Priorities.USER)            // runs after auth
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter { ... }
```

This declarative pipeline is cleaner than manually sequencing method calls inside resource classes.

**6. Non-Invasive Modification**

Filters can be added, removed, or swapped without touching any resource code. For example:
- Switch from `java.util.logging` to SLF4J → change one class.
- Disable logging in load tests → remove one `@Provider` registration.
- Add request-timing metrics → create a new filter with higher priority.

**7. Reusability Across Projects**

A well-designed filter can be packaged into a library JAR and dropped into any JAX-RS application. Manual logging code would need to be copied and adapted for each project.

**In summary**, JAX-RS filters provide a **declarative, centralised, consistent, and maintainable** approach to cross-cutting concerns. Manual logging is **imperative, scattered, error-prone, and fragile**. The filter pattern is the industry-standard approach recommended by the JAX-RS specification itself.

---
