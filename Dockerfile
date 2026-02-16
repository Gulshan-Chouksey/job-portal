# ---- Stage 1: Build ----
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn

# Download dependencies first (layer caching)
RUN ./mvnw dependency:go-offline -q

COPY src src

RUN ./mvnw clean package -DskipTests -q

# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
