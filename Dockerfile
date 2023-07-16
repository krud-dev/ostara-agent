FROM azul/zulu-openjdk:17-latest as build
ARG VERSION=unspecified
WORKDIR /tmp
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src/ src/

RUN ./gradlew --console=plain \
    -Pdocker \
    -Pversion=${VERSION}-DOCKER \
    bootJar

FROM azul/zulu-openjdk:17-latest as run
WORKDIR /app
COPY --from=build /tmp/build/libs/ostara-agent.jar app.jar
ENTRYPOINT ["java","-jar","app.jar", "--spring.profiles.active=docker"]
