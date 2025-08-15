# Build stage
FROM maven:3.9.4-eclipse-temurin-17 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/forex-aggregator-service-0.0.1-SNAPSHOT.jar forex-aggregator-service.jar
ENTRYPOINT ["java", "-jar", "forex-aggregator-service.jar"]
