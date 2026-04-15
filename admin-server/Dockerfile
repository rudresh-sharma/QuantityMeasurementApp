FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml pom.xml
COPY admin-server/pom.xml admin-server/pom.xml
COPY admin-server/src admin-server/src

RUN mvn -f admin-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/admin-server/target/*SNAPSHOT.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
