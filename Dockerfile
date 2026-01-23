FROM maven:3.9-eclipse-temurin-25

WORKDIR /app

COPY pom.xml ./
COPY .mvn ./.mvn
COPY mvnw ./

RUN mvn dependency:go-offline -B || true


EXPOSE 8080

CMD ["mvn", "spring-boot:run"]
