FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./
COPY checkstyle.xml ./
COPY .mvn ./.mvn
COPY mvnw ./

RUN mvn dependency:go-offline -B || true

COPY src ./src

RUN mvn clean package -DskipTests -Dmaven.antrun.skip=true -Dspotless.check.skip=true -B

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
