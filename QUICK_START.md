

### Setup Databases
CREATE DATABASE userdb;
CREATE DATABASE bookingdb;
CREATE DATABASE paymentdb;

MongoDB will auto-create its database

Configure Database Connections

For **user-service**, **booking-service**, and **payment-service**:
Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD (postgres)
```
Run 4 different services.

Open browser: `http://localhost:8081/actuator/health`
If a message is shown its up


## Service Ports
- User Service: **8081**
- Event Catalog: **8082**  
- Booking Service: **8083**
- Payment Service: **8084**

- Make sure PostgreSQL/MongoDB is running
- Check username/password in application.properties
- Right-click `pom.xml` → Maven → Reload Project



