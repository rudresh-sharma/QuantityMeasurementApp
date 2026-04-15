FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml pom.xml
COPY quantity-measurement-service/pom.xml quantity-measurement-service/pom.xml
COPY quantity-measurement-service/src quantity-measurement-service/src

RUN mvn -pl quantity-measurement-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/quantity-measurement-service/target/*SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
