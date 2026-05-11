FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S datasync && adduser -S datasync -G datasync
COPY --from=builder /app/target/*.jar app.jar
RUN chown datasync:datasync app.jar
USER datasync
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]