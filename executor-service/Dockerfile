# Use JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Spring Boot fat JAR (packaged with Spring Boot plugin)
COPY target/executor-service-0.0.1-SNAPSHOT.jar app1.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app1.jar"]