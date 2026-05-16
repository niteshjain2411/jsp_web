# Stage 1: Build using an official Maven image (Maven can compile down to target targets)
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Stage 2: Run using the actual Eclipse Temurin 24 image
FROM eclipse-temurin:24-jre
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]