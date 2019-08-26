FROM openjdk:8-jdk-slim AS builder

WORKDIR /opt/gatling

COPY build.gradle gradlew settings.gradle ./
COPY src src
COPY gradle gradle

RUN ./gradlew shadowJar

FROM openjdk:8-jre-slim

WORKDIR /opt/gatling

ENV GATLING_JAR=/opt/gatling/pelias-server-stress.jar
ENV SERVER_URL="" \
    REGIONS_FILE="" \
    SEEDS_FILE="" \
    USERS_COUNT="" \
    USERS_RAMP_TIME="" \
    GENERATE_SEEDS="" \
    AUTO_GENERATE_SEEDS=""

COPY --from=builder /opt/gatling/build/libs/pelias-server-stress-all.jar $GATLING_JAR
COPY bin/pelias-server-stress /bin/

CMD simulation.sh
