# Render Deployment Guide for EventPlanner Microservices

## Overview
This guide explains how to deploy the EventPlanner microservices to Render when team members use separate free accounts.

## Prerequisites
- A Render account (free tier works)
- Access to this GitHub repository
- PostgreSQL database (Render provides free tier or use external)
- MongoDB database (use MongoDB Atlas free tier)

## Service URLs
After deployment, each service gets a unique URL like:
- `https://user-service-xxxx.onrender.com`
- `https://event-catalog-service-yyyy.onrender.com`
- `https://booking-service-zzzz.onrender.com`
- `https://payment-service-wwww.onrender.com`

## Deployment Steps

### Step 1: Create a New Web Service on Render

1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click "New +" → "Web Service"
3. Connect your GitHub account and select `Comp301-Microservices-Project`
4. Configure the service:
   - **Name**: e.g., `user-service`
   - **Root Directory**: e.g., `user-service`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`

### Step 2: Configure Environment Variables

In the Render dashboard for each service, add these environment variables:

#### For ALL Services:
| Variable | Value |
|----------|-------|
| `PORT` | (Render sets automatically) |
| `EUREKA_ENABLED` | `false` |

#### For user-service:
| Variable | Value |
|----------|-------|
| `DB_HOST` | Your PostgreSQL host |
| `DB_PORT` | `5432` |
| `DB_NAME` | `userdb` |
| `DB_USERNAME` | Your DB username |
| `DB_PASSWORD` | Your DB password |

#### For event-catalog-service:
| Variable | Value |
|----------|-------|
| `MONGODB_URI` | Your MongoDB connection string |
| `MONGODB_DATABASE` | `eventcatalog` |

#### For booking-service:
| Variable | Value |
|----------|-------|
| `DB_HOST` | Your PostgreSQL host |
| `DB_PORT` | `5432` |
| `DB_NAME` | `bookingdb` |
| `DB_USERNAME` | Your DB username |
| `DB_PASSWORD` | Your DB password |
| `EVENT_SERVICE_URL` | `https://event-catalog-service-xxxx.onrender.com` |

#### For payment-service:
| Variable | Value |
|----------|-------|
| `DB_HOST` | Your PostgreSQL host |
| `DB_PORT` | `5432` |
| `DB_NAME` | `paymentdb` |
| `DB_USERNAME` | Your DB username |
| `DB_PASSWORD` | Your DB password |
| `BOOKING_SERVICE_URL` | `https://booking-service-xxxx.onrender.com` |

### Step 3: Deploy Order

Deploy services in this order:
1. **user-service** (no dependencies)
2. **event-catalog-service** (no dependencies)
3. **booking-service** (needs EVENT_SERVICE_URL)
4. **payment-service** (needs BOOKING_SERVICE_URL)

### Step 4: Update Service URLs

After booking-service deploys, copy its URL and update payment-service's `BOOKING_SERVICE_URL`.
After event-catalog-service deploys, copy its URL and update booking-service's `EVENT_SERVICE_URL`.

## Database Setup

### PostgreSQL (for user, booking, payment services)
Option 1: Use Render PostgreSQL (paid)
Option 2: Use [ElephantSQL](https://www.elephantsql.com/) free tier
Option 3: Use [Supabase](https://supabase.com/) free tier

### MongoDB (for event-catalog-service)
Use [MongoDB Atlas](https://www.mongodb.com/atlas) free tier:
1. Create a free cluster
2. Create a database user
3. Whitelist all IPs (0.0.0.0/0) for Render access
4. Get connection string and use as `MONGODB_URI`

## Testing Deployment

1. Check health endpoints:
   - `https://your-service-url.onrender.com/actuator/health`

2. Test API endpoints:
   - User registration: POST to `/api/users/register`
   - Create event: POST to `/api/events`
   - Create booking: POST to `/api/bookings`
   - Process payment: POST to `/api/payments`

## Troubleshooting

### Service won't start
- Check Render logs for errors
- Verify all environment variables are set
- Ensure database is accessible

### Services can't communicate
- Verify SERVICE_URL environment variables are correct
- Check that target service is running
- Ensure URLs include `https://`

### Database connection fails
- Whitelist Render's IP addresses (or use 0.0.0.0/0)
- Verify credentials are correct
- Check database is running

## Local Development

For local development, services use default localhost URLs:
```bash
# No environment variables needed locally
# Just run discovery-server first, then other services
cd discovery-server && mvn spring-boot:run
cd user-service && mvn spring-boot:run
# etc.
```
