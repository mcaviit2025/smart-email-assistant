# Stage 1: Build application with Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml and download dependencies (cached if pom.xml doesn't change)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable jar
COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (Render overrides with $PORT dynamically)
EXPOSE 8080

USER appuser

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
