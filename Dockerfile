FROM eclipse-temurin:21-jre
WORKDIR /app

ARG MODULE
COPY ${MODULE}/target/*SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
