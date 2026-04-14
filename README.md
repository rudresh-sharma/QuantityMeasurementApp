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

## Docker

Build the Spring Boot jars locally first:

```bash
mvn clean package -DskipTests
```

Then build and start the full stack with:

```bash
docker compose up --build
```

For Google OAuth in Docker, create `authentication-service/secrets/oauth-secrets.properties`
from the example file and put your real Google client ID and secret there before starting
the stack.

Published ports:

- `8080`: API gateway
- `8081`: authentication-service
- `8082`: quantity-measurement-service
- `8083`: user-service
- `8761`: Eureka dashboard
- `9090`: Spring Boot Admin
- `3306`: MySQL

The compose stack injects container-friendly `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`,
`USER_SERVICE_BASE_URL`, and datasource settings so the services can discover each other
inside Docker.

## Jenkins

This repository now includes a root [Jenkinsfile](./Jenkinsfile) for CI.

- It builds the full Maven multi-module project from the repository root.
- It archives generated Spring Boot jars from each service.
- It can also build Docker images for all services through the `BUILD_DOCKER_IMAGES` parameter.

Jenkins setup notes are in [JENKINS.md](./JENKINS.md).
