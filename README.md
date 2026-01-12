# EventPlanner Microservices – Quick Start & Deployment Instructions

## What is this project?

**EventPlanner Microservices** is the COMP 301 course project. It is a modern, microservice-based event planning app built with Spring Boot, featuring:

- **User Service**: Registration, auth, roles (PostgreSQL)
- **Event Catalog Service**: Event creation, search/filter (MongoDB)
- **Booking Service**: Book/cancel/confirm event tickets (PostgreSQL)
- **Payment Service**: Payments & refunds (PostgreSQL)
- **Service Discovery**: Eureka for seamless scaling, health, and discovery
- JWT-based authentication and Spring Security are used for all APIs.
- **CORS is intentionally disabled** (i.e., all origins are allowed) for easy cross-origin use (not recommended for production, but simplifies dev and deployment on Render.com).

---

## Running Locally

1. **Clone the repo** and open in IntelliJ IDEA.
2. **Set up databases:**
   - Create PostgreSQL databases for:
     - `userdb`
     - `bookingdb`
     - `paymentdb`
   - For MongoDB, create a database called `eventcatalog`.
     - MongoDB data will be created automatically, but ensure you have a running instance.
LOCALHOST VERSION IS ALSO AVAIBLE SO YOU DONT HAVE TO CHANGE ANY CODE!
3. **Configure credentials:**  
   Each service's `application.properties` will need:
   ```
   # PostgreSQL
   spring.datasource.url=jdbc:postgresql://localhost:5432/<db_name>
   spring.datasource.username=your_pg_user
   spring.datasource.password=your_pg_password
   # MongoDB
   spring.data.mongodb.uri=mongodb://localhost:27017/eventcatalog
   ```

4. **Start the Discovery Server first (port 8761) – wait until dashboard is up**

5. **Then, start the microservices (8081, 8082, 8083, 8084) in any order.**

---

## Deploying on Render.com (Cloud)

All services can be deployed individually as web services or background workers on [Render.com](https://render.com/).

**Environment Variables (use sensible secrets! Do NOT hardcode real passwords):**
```
# PostgreSQL example
SPRING_DATASOURCE_URL=jdbc:postgresql://<neon_db_host>:5432/<db_name>
SPRING_DATASOURCE_USERNAME=<db_user>
SPRING_DATASOURCE_PASSWORD=<db_password>
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# MongoDB example
SPRING_DATA_MONGODB_URI=mongodb+srv://<mongo_user>:<mongo_password>@<mongodb_host>/<db_name>

# Eureka
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://<discovery-service-host>:8761/eureka/

# JWT config
JWT_SECRET=<your_jwt_secret>

# Other configs
SPRING_PROFILES_ACTIVE=prod
```
Replace `<…>` with your values—**never commit secrets!**

- For Postgres on Render, you can use **Neon** as a host solution.
- For MongoDB, you can use MongoDB Atlas or Render's managed MongoDB.
- CORS is **disabled** by default. For security, enable it in production.

---

## How to Create Databases

**PostgreSQL (use psql, pgAdmin, or Neon/Render panel):**
```sql
CREATE DATABASE userdb;
CREATE DATABASE bookingdb;
CREATE DATABASE paymentdb;
```

**MongoDB (use MongoDB Atlas or local, Compass):**
- Create a database called `eventcatalog`.
- Collections are created automatically by Spring Data.

---

## Useful URLs (Health & API)

- Discovery (Eureka): http://localhost:8761/
- User Service: http://localhost:8081/
- Event Catalog: http://localhost:8082/
- Booking Service: http://localhost:8083/
- Payment Service: http://localhost:8084/

**Health checks:**  
- http://localhost:8081/actuator/health  
- http://localhost:8082/actuator/health  
- http://localhost:8083/actuator/health  
- http://localhost:8084/actuator/health  

**Sample Endpoints:**  
- User: `/api/users/register`, `/api/users/login`
- Events: `/api/events`, `/api/events/search?query=…`
- Booking: `/api/bookings`, `/api/bookings/user/{userId}`
- Payments: `/api/payments`, `/api/payments/{id}/refund`

---

## Tech Stack

- **Spring Boot 3.2+**, **Java 21**
- **PostgreSQL** (Neon for cloud recommended)
- **MongoDB** (Atlas or Render.com DB, or local)
- **Maven**
- **Spring Cloud (Eureka)**
- **JWT, Spring Security**
- CORS: **DISABLED** (dev only!)
