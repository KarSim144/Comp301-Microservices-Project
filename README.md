EventPlanner Microservices Application
A COMP 301 requirements project.

### Microservices

1. **User Service** (Port 8081)
   - User registration and authentication
   - JWT token generation
   - Role-based access control
   - PostgreSQL database

2. **Event Catalog Service** (Port 8082)
   - Event creation and management
   - Event search and filtering
   - Category-based organization
   - MongoDB database

3. **Booking Service** (Port 8083)
   - Ticket booking and management
   - Booking confirmation and cancellation
   - Integration with Event and Payment services
   - PostgreSQL database

4. **Payment Service** (Port 8084)
   - Payment processing
   - Transaction management
   - Refund handling
   - PostgreSQL database

### Prerequisites

- Java 17 or higher (21 preferred)
- Maven 3.6+
- PostgreSQL 13+ (17 preffered)
- MongoDB 5+ 
- IntelliJ IDEA project<

**Configure Databases**

   Create PostgreSQL databases from pgadmin4!
   
   CREATE DATABASE userdb;
   CREATE DATABASE bookingdb;
   CREATE DATABASE paymentdb;
   Download MongoDB and Compass they will be created automatically.

**Update application.properties for each service**

   For PostgreSQL services (user-service, booking-service, payment-service):
   spring.datasource.url=jdbc:postgresql://localhost:5432/[database_name]
   spring.datasource.username=postgres
   spring.datasource.password=your_password (postgres)

   For MongoDB (event-catalog-service):
   spring.data.mongodb.uri=mongodb://localhost:27017/eventcatalog
   
**Build and Run Services**

   Open each service folder in IntelliJ IDEA:
   - File → Open → Select service folder
   - Maven will auto-import dependencies
   - Run the main Application class

## 📡 API Endpoints

### User Service (http://localhost:8081)

```
POST   /api/users/register       - Register new user
POST   /api/users/login          - User login
GET    /api/users/{id}           - Get user by ID
GET    /api/users                - Get all users (Admin only)
DELETE /api/users/{id}           - Delete user (Admin only)
```

### Event Catalog Service (http://localhost:8082)

```
POST   /api/events               - Create event
GET    /api/events               - Get all events
GET    /api/events/{id}          - Get event by ID
PUT    /api/events/{id}          - Update event
DELETE /api/events/{id}          - Delete event
GET    /api/events/category/{category} - Get events by category
GET    /api/events/upcoming      - Get upcoming events
GET    /api/events/search?query= - Search events
```

### Booking Service (http://localhost:8083)

```
POST   /api/bookings             - Create booking
GET    /api/bookings/{id}        - Get booking by ID
GET    /api/bookings             - Get all bookings
GET    /api/bookings/user/{userId} - Get user bookings
POST   /api/bookings/{id}/confirm - Confirm booking
DELETE /api/bookings/{id}        - Cancel booking
```

### Payment Service (http://localhost:8084)

```
POST   /api/payments             - Process payment
GET    /api/payments/{id}        - Get payment by ID
GET    /api/payments             - Get all payments
GET    /api/payments/user/{userId} - Get user payments
GET    /api/payments/transaction/{txnId} - Get by transaction ID
POST   /api/payments/{id}/refund - Refund payment
```

## Testing

### Sample API Calls with curl
Use PostMan for API testing.


## Database Schemas

### User Service (PostgreSQL)
```sql
users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  phone_number VARCHAR(20),
  role VARCHAR(20) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)
```

### Event Catalog Service (MongoDB)
```javascript
{
  _id: ObjectId,
  title: String,
  description: String,
  category: String,
  eventDate: Date,
  location: String,
  capacity: Number,
  availableSeats: Number,
  price: Number,
  organizerId: String,
  imageUrl: String,
  status: String,
  createdAt: Date,
  updatedAt: Date
}
```

### Booking Service (PostgreSQL)
```sql
bookings (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  event_id VARCHAR(255) NOT NULL,
  number_of_tickets INTEGER NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL,
  payment_id VARCHAR(255),
  booking_date TIMESTAMP,
  updated_at TIMESTAMP
)
```

### Payment Service (PostgreSQL)
```sql
payments (
  id BIGSERIAL PRIMARY KEY,
  booking_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL,
  payment_method VARCHAR(50),
  transaction_id VARCHAR(255) UNIQUE,
  payment_date TIMESTAMP
)
```


## Technologies Used

- **Backend Framework:** Spring Boot 3.2.0
- **Java Version:** 21
- **Databases:** PostgreSQL, MongoDB
- **Security:** Spring Security, JWT
- **Build Tool:** Maven
- **API Documentation:** RESTful APIs




## Team Information
- Members: Berkay, Alp, Derin, Burak, Utku
- Project: EventPlanner Microservices
- Course: COMP 301 - Software Architectures and Tools
- Semester: Fall 2025

Health Checks:
- http://localhost:8081/actuator/health
- http://localhost:8082/actuator/health
- http://localhost:8083/actuator/health
- http://localhost:8084/actuator/health

View Data:
- http://localhost:8082/api/events
- http://localhost:8083/api/bookings
- http://localhost:8084/api/payments
- Rest are from Postman
