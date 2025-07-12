# Use Maven and JDK for building
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

# Build the app
RUN mvn clean package -DskipTests

# ==============================
# Run the app
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/project-tracker-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
