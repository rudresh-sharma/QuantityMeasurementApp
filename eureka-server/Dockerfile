FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY eureka-server/src eureka-server/src

RUN mvn -pl eureka-server -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/eureka-server/target/*SNAPSHOT.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
