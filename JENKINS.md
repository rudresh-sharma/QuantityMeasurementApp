# Jenkins Setup

This repository includes a root [Jenkinsfile](./Jenkinsfile) for the Spring Boot microservices build.

## What the pipeline does

- checks out the repository
- prints Java and Maven versions for easier debugging
- runs the root Maven build
- archives all generated `target/*.jar` files
- optionally builds Docker images for each service

## Recommended Jenkins job

- Create a `Pipeline` job.
- Point it to this repository.
- Use the repository root `Jenkinsfile`.
- Make sure the Jenkins agent has:
  - Java 21
  - Maven 3.9+
  - Docker, only if you want to enable `BUILD_DOCKER_IMAGES`

## Parameters

- `SKIP_TESTS`
  - `true` by default because the repository currently has no automated test classes.
- `BUILD_DOCKER_IMAGES`
  - `false` by default.
  - Turn it on only when the Jenkins agent can run Docker commands.

## Build command used

When `SKIP_TESTS=true`:

```bash
mvn -B -ntp clean package -DskipTests
```

When `SKIP_TESTS=false`:

```bash
mvn -B -ntp clean verify
```

## Next improvement

Once you add backend tests, the same pipeline can enforce them by running with `SKIP_TESTS=false`.
