FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /workspace/app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jdk-alpine AS production
WORKDIR /app

COPY --from=build /workspace/app/target/*.jar ./app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]