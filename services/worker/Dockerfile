FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY services/common/pom.xml services/common/
COPY services/worker/pom.xml services/worker/
COPY services/api/pom.xml services/api/

COPY services/common/src services/common/src
COPY services/worker/src services/worker/src

COPY settings.xml /root/.m2/settings.xml

RUN mvn clean package -pl services/worker -am -DskipTests -Dmaven.antrun.skip=true -Dspotless.check.skip=true -Dcheckstyle.skip=true -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/services/worker/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]