FROM eclipse-temurin:21-jdk AS builder

WORKDIR /opt/gatling

COPY build.gradle.kts gradlew settings.gradle.kts gradle.properties ./
COPY src src
COPY gradle gradle

RUN ./gradlew shadowJar

FROM eclipse-temurin:21-jre

WORKDIR /opt/gatling

ENV GATLING_JAR=/opt/gatling/pelias-server-stress.jar
ENV GATLING_PROPERTIES=
ENV SERVER_URL=
ENV REGIONS_FILE=
ENV SEEDS_FILE=
ENV USERS_COUNT=
ENV USERS_RAMP_TIME=
ENV GENERATE_SEEDS=
ENV AUTO_GENERATE_SEEDS=

COPY --from=builder /opt/gatling/build/libs/pelias-server-stress-all.jar $GATLING_JAR
COPY bin/pelias-server-stress /bin/

ENTRYPOINT ["/bin/pelias-server-stress"]
