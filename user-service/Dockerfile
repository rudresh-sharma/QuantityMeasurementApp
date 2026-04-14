FROM eclipse-temurin:21-jre
WORKDIR /app

COPY user-service/target/*SNAPSHOT.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
