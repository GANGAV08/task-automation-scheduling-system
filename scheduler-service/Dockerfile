# Use JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Spring Boot fat JAR (packaged with Spring Boot plugin)
COPY target/scheduler-service-0.0.1-SNAPSHOT.jar app.jar


# Run the app
#ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["sh", "-c", "sleep 60 && java -jar app.jar"]
