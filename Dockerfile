# Stage 1: Build the Spring Boot application
FROM eclipse-temurin:17-jdk-focal AS builder

WORKDIR /app

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn

# Copy Maven configuration
COPY pom.xml .

# Copy source code
COPY src src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the Spring Boot executable JAR
RUN ./mvnw clean package -DskipTests


# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy the generated JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Render will provide the PORT environment variable
EXPOSE 8080

# Start Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]