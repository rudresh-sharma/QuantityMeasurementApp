# Quantity Measurement App

A Spring Boot REST API for performing unit conversions and arithmetic across multiple measurement types (length, weight, volume, temperature), with JWT + Google OAuth2 authentication and full operation history tracking.

## Features

- **Compare, convert, add, subtract, and divide** quantities across measurement types
- **Multi-unit support**:
  - Length — kilometer, meter, centimeter, millimeter, mile, yard, foot, inch
  - Weight — kilogram, gram, milligram, pound, ounce, tonne
  - Volume — litre, millilitre, gallon, quart, pint, cup, fluid ounce, cubic meter
  - Temperature — Celsius, Fahrenheit, Kelvin (conversion only; arithmetic not supported on absolute temperature values)
- **Operation history** — query past operations by type, by measurement type, get success counts, or view error history
- **Authentication** — register/login with email + password (JWT-based), or sign in with Google (OAuth2)
- **API documentation** — interactive Swagger UI generated automatically from the codebase
- **Environment-agnostic config** — same codebase runs against local MySQL in development and TiDB Cloud in production via Spring profiles

## Tech Stack

- **Java 21**
- **Spring Boot 4.0** (Web MVC, Data JPA, Security, OAuth2 Client, Validation, Actuator)
- **MySQL** (TiDB Cloud in production)
- **JWT** via `jjwt`
- **springdoc-openapi** for Swagger UI
- **Lombok**
- **Maven** (with Maven Wrapper)
- **Docker** (multi-stage build for deployment)

## Project Structure

```
src/main/java/com/app/quantitymeasurementapp/
├── config/          # Security configuration
├── controller/       # REST controllers (Auth, OAuth success, Quantity Measurement)
├── dto/               # Request/response DTOs
├── exception/         # Custom exceptions and global exception handling
├── model/             # JPA entities
├── repository/        # Spring Data JPA repositories
├── security/           # JWT filter/service, OAuth2 success handler, user details service
├── service/            # Business logic
└── unit/               # Measurement unit enums and conversion logic
```

## Getting Started

### Prerequisites
- Java 21
- MySQL running locally (or access to a remote MySQL/TiDB instance)
- A Google OAuth2 client ID and secret (for Google sign-in)

### Clone and build
```bash
git clone https://github.com/rudresh-sharma/QuantityMeasurementApp.git
cd QuantityMeasurementApp
./mvnw clean package
```

### Configuration

The app uses Spring profiles to separate environments. Set required values as environment variables — no source code changes are needed to switch between local and production.

| Variable | Description |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `local` or `prod` |
| `SPRING_DATASOURCE_URL` | JDBC URL of the database |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key used to sign JWTs |
| `JWT_EXPIRATION_MINUTES` | Token expiration, in minutes |
| `FRONTEND_ORIGIN` | Allowed CORS origin for the frontend |
| `FRONTEND_GOOGLE_REDIRECT` | Redirect URL after successful Google login |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID` | Google OAuth2 client ID |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret |
| `PORT` | Server port (defaults to `8080`) |

> Never commit real credentials. Keep them in environment variables or a gitignored local secrets file.

### Run locally
```bash
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`.

### Run with Docker
```bash
docker build -t quantity-measurement-app .
docker run -p 8080:8080 --env-file .env quantity-measurement-app
```

## API Documentation

Once running, interactive API docs are available at:
```
http://localhost:8080/swagger-ui.html
```

Raw OpenAPI spec:
```
http://localhost:8080/v3/api-docs
```

## Main Endpoints

### Authentication — `/api/v1/auth`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Register a new local user |
| POST | `/login` | Login with email and password |
| GET | `/me` | Get the currently authenticated user |

Google sign-in is handled via Spring Security's OAuth2 login flow (`/oauth2/authorization/google`).

### Quantity Measurements — `/api/v1/quantities`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/compare` | Compare two quantities |
| POST | `/convert` | Convert a quantity to a target unit |
| POST | `/add` | Add two quantities |
| POST | `/subtract` | Subtract two quantities |
| POST | `/divide` | Divide two quantities |
| GET | `/history/operation/{operation}` | Get measurement history by operation type |
| GET | `/history/type/{measurementType}` | Get measurement history by measurement type |
| GET | `/count/{operation}` | Get count of successful operations by type |
| GET | `/history/errored` | Get error history |

## Deployment

The included `Dockerfile` uses a multi-stage build (Maven + Java 21 build stage, slim JRE runtime stage) and is deployed on [Render](https://render.com). Set the environment variables listed above in your hosting platform's dashboard — production uses a TiDB Cloud MySQL-compatible database, while local development points at a local MySQL instance, controlled entirely by the active Spring profile.

## License

See [LICENSE](./LICENSE) for details.
