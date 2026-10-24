
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

- [📖 Project Overview](#-project-overview)
- [✨ Key Features](#-key-features)
- [🛠️ Tech Stack](#-tech-stack)
- [🏗️ Architecture at a Glance](#-architecture-at-a-glance)
- [🚀 Getting Started](#-getting-started)
- [🌐 Deployment & API Documentation](#-deployment--api-documentation)

</details>

---

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

