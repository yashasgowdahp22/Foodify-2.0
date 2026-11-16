# Stage 1: Build the WAR artifact from source
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.war app.war

EXPOSE 8080

# Run Spring Boot executable WAR with container memory management
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -Xmx350m -Xms128m -jar app.war"]
