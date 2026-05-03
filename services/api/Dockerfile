FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./
COPY .mvn ./.mvn
COPY mvnw ./

COPY settings.xml /root/.m2/settings.xml

COPY services/common/pom.xml services/common/
COPY services/worker/pom.xml services/worker/
COPY services/api/pom.xml services/api/

RUN mvn -pl services/api dependency:go-offline -B || true

COPY services/api/src ./services/api/src
COPY services/common/src services/common/src

RUN mvn -pl services/api -am clean package -DskipTests -Dmaven.antrun.skip=true -Dspotless.check.skip=true -Dcheckstyle.skip=true -B

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/services/api/target/*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
