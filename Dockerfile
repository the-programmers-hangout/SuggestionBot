FROM gradle:6.7.0-jdk15 AS build
COPY --chown=gradle:gradle . /suggestions
WORKDIR /suggestions
RUN gradle shadowJar --no-daemon

FROM openjdk:8-jre-slim
RUN mkdir /config/
COPY --from=build /suggestions/build/libs/Suggestions.jar /

ENTRYPOINT ["java", "-jar", "/Suggestions.jar"]
