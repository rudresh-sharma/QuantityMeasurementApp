FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml pom.xml
COPY authentication-service/pom.xml authentication-service/pom.xml
COPY authentication-service/src authentication-service/src
COPY authentication-service/secrets authentication-service/secrets

RUN mvn -f authentication-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/authentication-service/target/*SNAPSHOT.jar app.jar
COPY authentication-service/secrets /app/secrets

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
