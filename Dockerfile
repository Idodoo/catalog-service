FROM maven:3.9.5-eclipse-temurin-21 AS build

LABEL maintainer="Ivan Dodoo <dodoo.ivan17@gmail.com>"

WORKDIR /workspace/app



COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /workspace/app/target/*.jar ./app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]