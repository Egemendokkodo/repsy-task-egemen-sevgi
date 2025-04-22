# Builder stage
FROM openjdk:17-jdk-slim AS builder

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml . 
COPY src ./src

# Build the application
RUN mvn clean install -DskipTests

# Start a new stage to reduce the image size
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the jar file from the builder stage
COPY --from=builder /app/target/*.jar /app/egemen_sevgi_docker.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/egemen_sevgi_docker.jar"]
