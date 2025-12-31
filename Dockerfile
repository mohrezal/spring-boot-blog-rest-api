FROM gradle:9.1-jdk25

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon || true


EXPOSE 8080

CMD ["gradle", "bootRun", "--no-daemon"]