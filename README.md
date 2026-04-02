# Quantity Measurement Platform

This branch is the starting point for migrating the existing monolith into five Spring Boot microservices:

- `admin-server`
- `eureka-server`
- `api-gateway`
- `authentication-service`
- `quantity-measurement-service`

## Current migration approach

- The original monolith source under the root `src/` folder is kept as the reference implementation.
- The root `pom.xml` is now a parent aggregator for the new microservice modules.
- New modules are scaffolded first, and code will be moved from the monolith into each service gradually.

## Initial service boundaries

- `authentication-service`
  Owns registration, login, JWT, OAuth2, user profile, and user persistence.
- `quantity-measurement-service`
  Owns quantity operations, unit conversion, history, and measurement persistence.
- `api-gateway`
  Receives client requests and routes them to the business services.
- `eureka-server`
  Registers and discovers service instances.
- `admin-server`
  Monitors service health and status.

## Next extraction targets

- Move `controller/AuthController` and auth-related classes into `authentication-service`.
- Move `controller/QuantityMeasurementController` and measurement logic into `quantity-measurement-service`.
- Keep JWT verification in downstream services and centralize login flows in `authentication-service`.
