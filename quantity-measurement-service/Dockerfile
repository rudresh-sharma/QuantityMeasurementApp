FROM eclipse-temurin:21-jre
WORKDIR /app

COPY quantity-measurement-service/target/*SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
