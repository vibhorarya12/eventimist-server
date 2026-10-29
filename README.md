<div align="center">
<pre>
 _____                 _   _           _     _
| ____|_   _____ _ __ | |_(_)_ __ ___ (_)___| |_
|  _| \ \ / / _ \ '_ \| __| | '_ ` _ \| / __| __|
| |___ \ V /  __/ | | | |_| | | | | | | \__ \ |_
|_____| \_/ \___|_| |_|\__|_|_| |_| |_|_|___/\__|
  ____                            _
 / ___|  ___ _ ____   _____ _ __ | |
 \___ \ / _ \ '__\ \ / / _ \ '__|| |
  ___) |  __/ |   \ V /  __/ |   |_|
 |____/ \___|_|    \_/ \___|_|   (_)
</pre>

### Backend API for Eventimist

*Powering event discovery, organizer workflows, and AI-assisted experiences.*

</div>

---

<details>
<summary><strong>📑 Table of Contents</strong></summary>

<br>

- [📖 Project Overview](#project-overview)
- [✨ Key Features](#key-features)
- [🛠️ Tech Stack](#tech-stack)
- [🏗️ Architecture at a Glance](#architecture)
- [🚀 Getting Started](#getting-started)
- [🌐 Deployment & API Documentation](#deployment)

</details>

---

<a id="project-overview"></a>
## 📖 Project Overview

**Eventimist Server** is the backend REST API for Eventimist, an event management platform designed to simplify event discovery and organizer workflows. Built with Java and Spring Boot, it provides secure API endpoints for event management, location-based discovery, and AI-assisted event experiences.

The backend integrates PostgreSQL with PostGIS for geospatial data, Redis for caching, and supporting services for authentication, media management, asynchronous messaging, and AI-powered functionality.

### 🔗 Visit Eventimist

Explore the live application: **[https://eventimist.vercel.app/](https://eventimist.vercel.app/)**

<div>
  <a href="https://eventimist.vercel.app/">
    <img
      width="700"
      alt="Eventimist application preview"
      src="https://github.com/user-attachments/assets/dc389e21-36ab-4b06-8315-eadf3603eb6e"
    />
  </a>
</div>

<a id="key-features"></a>
## ✨ Key Features

- 🎟️ **Event Management** — Create, update, publish, and manage events through organizer APIs.
- 🔎 **Event Discovery** — Retrieve published events through public REST APIs.
- 📍 **Location-Based Discovery** — Find nearby events using geospatial queries powered by PostgreSQL and PostGIS.
- 🔐 **Authentication & Authorization** — Secure user and organizer endpoints using Spring Security and JWT access and refresh tokens.
- ⚡ **Redis Caching** — Cache event data to reduce repeated database queries and improve response times.
- 🤖 **AI-Assisted Features** — Generate event content and support AI-powered event workflows using Spring AI.
- 🖼️ **Media Management** — Store and manage event images using Cloudinary.
- 📨 **Asynchronous Processing** — Use RabbitMQ for queued background tasks such as email processing.
- 🐳 **Dockerized Deployment** — Package the Spring Boot application into a Docker image for deployment.
- 📄 **API Documentation** — Explore and test REST endpoints through Swagger/OpenAPI, when enabled.

<a id="tech-stack"></a>
## 🛠️ Tech Stack

| Category | Technologies |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.9 |
| **Security & Authentication** | Spring Security, JWT |
| **Database & Geospatial Queries** | PostgreSQL, PostGIS, Spring Data JPA, Hibernate Spatial |
| **Caching** | Redis |
| **AI Integration** | Spring AI, OpenAI-compatible APIs |
| **Messaging & Background Processing** | RabbitMQ |
| **Media Storage** | Cloudinary |
| **Build Tool** | Maven |
| **Containerization & Deployment** | Docker, Render |
| **API Documentation** | Swagger / OpenAPI |

<a id="architecture"></a>
## 🏗️ Architecture at a Glance

Eventimist Server follows a layered Spring Boot architecture, separating API handling, business logic, data access, and integrations with external services.

```text
                     ┌──────────────────────┐
                     │  Eventimist Frontend │
                     │     Next.js / Vercel │
                     └──────────┬───────────┘
                                │ HTTPS / REST API
                                ▼
                     ┌──────────────────────┐
                     │  Spring Boot Backend │
                     │    Java 17 / Render  │
                     └──────────┬───────────┘
                                │
               ┌────────────────┼────────────────┐
               ▼                ▼                ▼
       ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
       │   Security   │ │  Controllers │ │  AI Services │
       │ Spring + JWT │ │  REST APIs   │ │ Spring AI    │
       └──────────────┘ └──────┬───────┘ └──────┬───────┘
                               │                │
                               ▼                ▼
                      ┌────────────────┐ ┌────────────────┐
                      │ Service Layer  │ │  DeepSeek API  │
                      │ Business Logic │ └────────────────┘
                      └───────┬────────┘
                              │
               ┌──────────────┼──────────────┐
               ▼              ▼              ▼
       ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
       │ PostgreSQL   │ │    Redis     │ │  RabbitMQ    │
       │ + PostGIS    │ │    Cache     │ │ Async Tasks  │
       └──────────────┘ └──────────────┘ └──────────────┘
                     ┌──────────────────────┐
                     │      Cloudinary      │
                     │    Media Storage     │
                     └──────────────────────┘
```

<a id="getting-started"></a>
## 🚀 Getting Started

Follow these steps to set up and run Eventimist Server locally.

### 📋 Prerequisites

Ensure you have the following installed:

- **Java 17** or later
- **Maven** (or use the included Maven Wrapper)
- **PostgreSQL** with the PostGIS extension
- **Redis**
- **RabbitMQ** (for asynchronous processing)

You'll also need credentials for the external services you use, such as Cloudinary and the DeepSeek API.

### 📥 1. Clone the Repository

```bash
git clone https://github.com/vibhorarya12/eventimist-server.git
cd eventimist-server
```

### ⚙️ 2. Configure Environment Variables

Create a `.env` file in the project root or configure the required variables in your IDE's run configuration.

Add your database credentials, JWT secrets, Redis and RabbitMQ settings, and external service API keys.

> Keep credentials private. Never commit your `.env` file to Git.

**Note:** Spring Boot does not automatically load a `.env` file by default. Ensure your environment variables are loaded through your IDE, shell, or an appropriate configuration library.

### 🗄️ 3. Configure the Database

Create a PostgreSQL database and enable PostGIS:

```sql
CREATE DATABASE eventimist;
```

Connect to the `eventimist` database and run:

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
```

Configure your database connection details in the application environment.

### ▶️ 4. Run the Application

Using the Maven Wrapper:

**Windows**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**

```bash
./mvnw spring-boot:run
```

Alternatively, run the application from your IDE.

Once started, the backend should be available at:

`http://localhost:8080`

### ✅ 5. Verify the Application

Check the health endpoint:

[http://localhost:8080/api/public/health](http://localhost:8080/api/public/health)

A successful response confirms that the health endpoint is reachable.

<a id="deployment"></a>
## 🌐 Deployment & API Documentation

## 🔖 API Documentation

<details>
<summary>🔐 Organizer Authentication</summary>

#### 1. Organizer Login

Authenticates an organizer and returns their profile information along with access and refresh tokens.

| Property | Details |
|---|---|
| **Endpoint** | `/api/auth/organizer/login` |
| **Method** | `POST` |
| **Content-Type** | `application/json` |
| **Authentication** | Not required |

**Request Body**

```json
{
  "email": "organizer@example.com",
  "password": "your-password"
}
```

**Success Response — `200 OK`**

```json
{
  "name": "John Doe",
  "email": "organizer@example.com",
  "bio": "Technology and community event organizer.",
  "profilePic": "https://example.com/profile.jpg",
  "token": "<access-token>",
  "refreshToken": "<refresh-token>",
  "coverImage": "https://example.com/cover.jpg",
  "location": "Pune, India"
}
```

**Response Fields**

| Field | Type | Description |
|---|---|---|
| `name` | `String` | Organizer's name |
| `email` | `String` | Organizer's email address |
| `bio` | `String` | Organizer's biography |
| `profilePic` | `String` | Profile picture URL |
| `token` | `String` | Authentication token |
| `refreshToken` | `String` | Token used to refresh authentication |
| `coverImage` | `String` | Cover image URL |
| `location` | `String` | Organizer's location |


**Error Responses**

**`404 Not Found` — Organizer Not Found**

```json
{
  "statusCode": 404,
  "message": "Organizer not found",
  "timestamp": "2026-10-25T10:30:00.000+00:00"
}
```

**`401 Unauthorized` — Invalid Credentials**

```json
{
  "statusCode": 401,
  "message": "Invalid credentials",
  "timestamp": "2026-10-25T10:30:00.000+00:00"
}
```

| Status Code | Exception | Description |
|---|---|---|
| `404 Not Found` | `EntityNotFoundException` | The requested organizer could not be found. |
| `401 Unauthorized` | `WrongCredentialsException` | The supplied login credentials are incorrect. |


---

#### 2. Organizer Registration

Registers a new organizer using their details and a valid Clerk session ID. Returns the organizer profile along with authentication tokens.

| Property | Details |
|---|---|
| **Endpoint** | `/api/auth/organizer/register` |
| **Method** | `POST` |
| **Content-Type** | `application/json` |
| **Authentication** | Not specified by the controller |

**Request Body**

```json
{
  "name": "John Doe",
  "email": "organizer@example.com",
  "password": "your-password",
  "clerkSessionId": "<clerk-session-id>"
}
```

**Request Fields**

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | `String` | Yes | Organizer's name |
| `email` | `String` | Yes | Organizer's email address |
| `password` | `String` | Yes | Organizer's password |
| `clerkSessionId` | `String` | Yes | Clerk session ID used during registration |

**Success Response — `200 OK`**

```json
{
  "name": "John Doe",
  "email": "organizer@example.com",
  "bio": "Technology and community event organizer.",
  "profilePic": "https://example.com/profile.jpg",
  "token": "<access-token>",
  "refreshToken": "<refresh-token>",
  "coverImage": "https://example.com/cover.jpg",
  "location": "Pune, India"
}
```

**Possible Error Responses**

| Status Code | Exception / Condition | Description |
|---|---|---|
| `400 Bad Request` | Validation failure | One or more required fields are blank. |
| `404 Not Found` | `EntityNotFoundException` | A required entity could not be found, if thrown during registration. |
| `401 Unauthorized` | `WrongCredentialsException` | An authentication-related error occurred, if thrown during registration. |

**Validation Messages**

- `Name is required.`
- `Email is required.`
- `Password is required.`
- `No valid clerk session id provided`


#### 3. Organizer OAuth Login

Authenticates an organizer using their email and Clerk session ID. The Clerk session is validated before the organizer is retrieved and authenticated.

| Property | Details |
|---|---|
| **Endpoint** | `/api/auth/organizer/oauthLogin` |
| **Method** | `POST` |
| **Content-Type** | `application/json` |
| **Authentication** | Valid Clerk session required |

**Request Body**

```json
{
  "email": "organizer@example.com",
  "clerkSessionId": "<clerk-session-id>"
}
```

**Request Fields**

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | `String` | Yes | Email associated with the organizer's account |
| `clerkSessionId` | `String` | Yes | Clerk session ID validated during login |

**Success Response — `200 OK`**

```json
{
  "name": "John Doe",
  "email": "organizer@example.com",
  "bio": "Technology and community event organizer.",
  "profilePic": "https://example.com/profile.jpg",
  "token": "<access-token>",
  "refreshToken": "<refresh-token>",
  "coverImage": "https://example.com/cover.jpg",
  "location": "Pune, India"
}
```

**Error Responses**

**`404 Not Found` — Clerk Session Authentication Failed**

```json
{
  "statusCode": 404,
  "message": "Clerk session authentication failed || session is inactive.",
  "timestamp": "2026-10-25T10:30:00.000+00:00"
}
```

**`404 Not Found` — Organizer Not Found**

```json
{
  "statusCode": 404,
  "message": "organizer not found , please register first !!",
  "timestamp": "2026-10-25T10:30:00.000+00:00"
}
```

| Status Code | Exception / Condition | Description |
|---|---|---|
| `400 Bad Request` | Validation failure | Email or Clerk session ID is blank. |
| `404 Not Found` | `ClerkAuthSessionException` | Clerk session authentication failed or the session is inactive. |
| `404 Not Found` | `EntityNotFoundException` | No organizer exists for the supplied email. |



#### 4. Organizer OAuth Registration

Registers a new organizer using their profile details and a valid Clerk session. The service checks for an existing account, creates the organizer, assigns a default free subscription, and returns authentication tokens.

| Property | Details |
|---|---|
| **Endpoint** | `/api/auth/organizer/oauthRegister` |
| **Method** | `POST` |
| **Content-Type** | `application/json` |
| **Authentication** | Valid Clerk session required |

**Request Body**

```json
{
  "name": "John Doe",
  "email": "organizer@example.com",
  "profilePic": "https://example.com/profile.jpg",
  "clerkSessionId": "<clerk-session-id>"
}
```

**Request Fields**

| Field | Type | Description |
|---|---|---|
| `name` | `String` | Organizer's name |
| `email` | `String` | Organizer's email address |
| `profilePic` | `String` | URL of the organizer's profile picture |
| `clerkSessionId` | `String` | Clerk session ID validated during registration |

> **Note:** Confirm the required fields and validation rules against `OrganizerOauthRegisterDTO`; its definition wasn't included in the provided code.

**Success Response — `200 OK`**

```json
{
  "name": "John Doe",
  "email": "organizer@example.com",
  "bio": null,
  "profilePic": "https://example.com/profile.jpg",
  "token": "<access-token>",
  "refreshToken": "<refresh-token>",
  "coverImage": null,
  "location": null
}
```

**Error Responses**

| Status Code | Exception / Condition | Description |
|---|---|---|
| `400 Bad Request` | Validation failure | The request fails `@Valid` validation. |
| `404 Not Found` | `ClerkAuthSessionException` | Clerk session authentication fails or the session is inactive. |
| Depends on handler | `ExistingEntityException` | An organizer with the supplied email already exists. |

**Example Error Response — Invalid Clerk Session**

```json
{
  "statusCode": 404,
  "message": "Clerk session authentication failed || session is inactive.",
  "timestamp": "2026-10-25T10:30:00.000+00:00"
}
```


#### 5. Refresh Access Token

Generates a new access token and refresh token using a valid refresh token. The existing refresh token is revoked after successful validation, implementing refresh token rotation.

| Property | Details |
|---|---|
| **Endpoint** | `/api/auth/organizer/refresh-token` |
| **Method** | `POST` |
| **Content-Type** | `application/json` |
| **Authentication** | Refresh token required |

**Request Body**

```json
{
  "refreshToken": "<refresh-token>"
}
```

**Success Response — `200 OK`**

```json
{
  "name": "John Doe",
  "email": "organizer@example.com",
  "bio": "Technology and community event organizer.",
  "profilePic": "https://example.com/profile.jpg",
  "token": "<new-access-token>",
  "refreshToken": "<new-refresh-token>",
  "coverImage": "https://example.com/cover.jpg",
  "location": "Pune, India"
}
```

**Response Fields**

The response uses `OrganizerAuthResponseDTO` and includes the organizer's profile information, a newly generated access token, and a newly generated refresh token.

**Error Responses**

| Condition | Exception Message |
|---|---|
| Refresh token not found or invalid | `Invalid refresh token` |
| Refresh token revoked | `Refresh token has been revoked` |
| Refresh token expired | `Refresh token has expired` |



#### 6. Organizer Logout

Logs out an organizer by revoking the provided refresh token, preventing it from being used to refresh authentication tokens.

| Property | Details |
|---|---|
| **Endpoint** | `/api/auth/organizer/logout` |
| **Method** | `POST` |
| **Content-Type** | `application/json` |
| **Authentication** | Refresh token required |

**Request Body**

```json
{
  "refreshToken": "<refresh-token>"
}
```

**Success Response — `200 OK`**

```json
{
  "message": "Logged out successfully"
}
```

**Behavior**

- Hashes the supplied refresh token before looking it up.
- Revokes the token if it exists.
- Returns a success response even if the token is not found.


## 🔐 Organizer Authentication Flow

```text
                    EVENTIMIST FRONTEND
                       Next.js / Vercel
                              |
                 +------------+-------------+
                 |                          |
                 v                          v
          Standard Login /             Clerk OAuth
          Registration                     |
                 |                          v
                 |                   Clerk Session
                 |                          |
                 |                    Email + Session ID
                 |                          |
                 +------------+-------------+
                              |
                              v
                ORGANIZER AUTH REST API
                 /api/auth/organizer
                              |
                              v
                  OrganizerAuthService
                              |
                 +------------+-------------+
                 |                          |
                 v                          v
          Standard Auth Flow          Clerk OAuth Flow
          Login / Register          oauthLogin / oauthRegister
                 |                          |
                 |                          v
                 |                   ClerkAuthService
                 |                          |
                 |                          v
                 |                 Verify Clerk Session
                 |                          |
                 +------------+-------------+
                              |
                              v
                    Organizer Repository
                              |
                              v
                         PostgreSQL
                    Find / Create Organizer
                              |
                              v
                    Generate Backend JWTs
                    Access + Refresh Tokens
                              |
                              v
                   OrganizerAuthResponseDTO
                    Profile + Tokens
                              |
                              v
                       FRONTEND CLIENT
                              |
                 +------------+-------------+
                 |                          |
                 v                          v
          Access Token                Refresh Token
                 |                          |
                 v                          v
       Protected Backend APIs       Refresh Token API
                 |                          |
                 v                          v
        Spring Security JWT       Validate, Revoke Old,
              Filter               Issue New Token Pair
```
</details>

<details>
<summary>🎟️ Organizer Actions</summary>

### 1. Create Event

Creates an event for the authenticated organizer. The event is saved with `DRAFT` status.

- **Endpoint:** `POST /api/organizer/create-event`
- **Content-Type:** `multipart/form-data`
- **Success Status:** `201 Created`

**Request Body**

In Postman, select **Body → form-data**. Add the following fields as text, except `files`, which should use the File type.

| Key | Example Value |
|---|---|
| `title` | AI & Innovation Meetup |
| `description` | An event exploring AI and emerging technologies. |
| `category` | TECH |
| `startTime` | 2026-11-15T10:00:00 |
| `endTime` | 2026-11-15T16:00:00 |
| `timezone` | Asia/Kolkata |
| `mode` | OFFLINE |
| `venue` | Pune Convention Centre |
| `onlineLink` | Leave empty for OFFLINE |
| `latitude` | 18.5204 |
| `longitude` | 73.8567 |
| `tags` | AI |
| `tags` | Technology |
| `capacity` | 100 |
| `ticketPrice` | 299.99 |
| `isFree` | false |
| `files` | Select an image file (up to 5 files) |

**Success Response — `201 Created`**

The controller currently returns the submitted `CreateEventDTO`, not the saved entity. Therefore, the response will resemble:

```json
{
  "title": "AI & Innovation Meetup",
  "description": "An event exploring AI and emerging technologies.",
  "category": "TECH",
  "startTime": "2026-11-15T10:00:00",
  "endTime": "2026-11-15T16:00:00",
  "timezone": "Asia/Kolkata",
  "mode": "OFFLINE",
  "venue": "Pune Convention Centre",
  "onlineLink": null,
  "latitude": 18.5204,
  "longitude": 73.8567,
  "coverImage": null,
  "images": null,
  "tags": [
    "AI",
    "Technology"
  ],
  "capacity": 100,
  "ticketPrice": 299.99,
  "isFree": false
}
```

**Notes**
- The `files` multipart field is not included in the JSON response because it is a separate controller parameter.
- Although `ticketPrice` and `isFree` appear in the response, your current service implementation does not persist them.
- The service generates image URLs from uploaded files and assigns the first uploaded image as the cover image, but those generated values won't appear in this DTO response.
- The exact serialized response depends on your DTO and Jackson configuration.


### 2. Get Organizer Events

Retrieves all events belonging to the currently authenticated organizer. The organizer ID is extracted from the authenticated user's context, so it does not need to be passed as a request parameter.

- **Endpoint:** `GET /api/organizer/events`
- **Authentication:** Required
- **Success Status:** `200 OK`
- **Content-Type:** `application/json`

**Request**

No request body or query parameters are required.

```http
GET /api/organizer/events
Authorization: Bearer <access-token>
```

**Success Response — `200 OK`**

Returns a list of events belonging to the authenticated organizer.

```json
[
  {
    "id": 77,
    "title": "AI & Innovation Meetup",
    "description": "An event exploring AI and emerging technologies.",
    "category": "TECH",
    "startTime": "2026-11-15T10:00:00",
    "endTime": "2026-11-15T16:00:00",
    "timezone": "Asia/Kolkata",
    "mode": "OFFLINE",
    "venue": "Pune Convention Centre",
    "onlineLink": null,
    "latitude": 18.5204,
    "longitude": 73.8567,
    "coverImage": "https://example.com/event-cover.jpg",
    "images": [
      "https://example.com/event-cover.jpg"
    ],
    "tags": [
      "AI",
      "Technology"
    ],
    "capacity": 100,
    "ticketPrice": 299.99,
    "isFree": false,
    "status": "DRAFT"
  }
]
```

*The JSON above is illustrative. The actual fields depend on `GetEventsResponseDTO` and `mapToGetEventsResponseDTO()`.*

**Empty Response — `200 OK`**

If the organizer has no events, the API returns an empty JSON array:

```json
[]
```

**Behavior**
- Retrieves events using the authenticated organizer's ID.
- Maps each `EventEntity` to `GetEventsResponseDTO`.
- Returns an empty list when no events are found.
- Does not accept an organizer ID from the client, helping prevent users from requesting another organizer's events by changing an ID parameter.


### 3. Update Event

Updates an existing event owned by the authenticated organizer, including event details, schedule, location, ticketing information, tags, and images.

- **Endpoint:** `PUT /api/organizer/events/{id}`
- **Authentication:** Required
- **Content-Type:** `multipart/form-data`
- **Success Status:** `200 OK`

**Request**

Pass the event ID in the URL and submit the fields using Postman → **Body → form-data**.

```http
PUT /api/organizer/events/77
Authorization: Bearer <access-token>
```

**Request Fields**

| Key | Example Value | Required |
|---|---|---|
| `title` | AI & Innovation Meetup — Updated | Yes |
| `description` | An updated event description. | Yes |
| `category` | TECH | Yes |
| `startTime` | 2026-11-15T10:00:00 | Yes |
| `endTime` | 2026-11-15T16:00:00 | Yes |
| `timezone` | Asia/Kolkata | Yes |
| `mode` | OFFLINE | Yes |
| `venue` | Pune Convention Centre | Conditional |
| `onlineLink` | https://example.com/meeting | Conditional |
| `latitude` | 18.5204 | Yes |
| `longitude` | 73.8567 | Yes |
| `tags` | AI | No |
| `tags` | Technology | No |
| `capacity` | 150 | No |
| `isFree` | false | Yes |
| `ticketPrice` | 399.99 | No |
| `existingImages` | https://example.com/keep.jpg | No |
| `newImages` | Select an image file | No |

**Image Handling**
- `existingImages`: URLs of existing images to retain.
- `newImages`: New image files to upload.
- Use multiple `existingImages` entries to retain multiple images and attach multiple `newImages` files to upload several images.

**Success Response — `200 OK`**

```json
{
  "status": "success",
  "message": "Event updated successfully",
  "timestamp": "2026-10-28T12:00:00"
}
```

**Behavior**
- Verifies event ownership against the authenticated organizer.
- Updates event details, schedule, location, tags, and ticketing fields.
- Handles retained and newly uploaded images.
- Updates the event's modification timestamp.
- Saves the event in PostgreSQL and refreshes its Redis cache.

**Notes**
- `venue` is required for `OFFLINE` and `HYBRID` events.
- `onlineLink` is required for `ONLINE` and `HYBRID` events.
- `ticketPrice` should be provided when `isFree` is `false`.
- The DTO requires all core fields, including latitude and longitude, so this endpoint currently behaves like a full update rather than a partial update.

</details>
