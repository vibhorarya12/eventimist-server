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

The backend is containerized with Docker and deployed on Render.

- **Base URL:** `https://<your-render-service>.onrender.com`
- **Swagger / OpenAPI:** `https://<your-render-service>.onrender.com/swagger-ui/index.html` (when enabled)

Replace the placeholder URLs above with your actual deployment details.
