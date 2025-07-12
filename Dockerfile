# Start from an OpenJDK base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Add the JAR file to the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Set environment variables to be configured at runtime
ENV SPRING_PROFILES_ACTIVE=prod

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
