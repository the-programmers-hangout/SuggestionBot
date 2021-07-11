FROM gradle:7.1-jdk16 AS build
COPY --chown=gradle:gradle . /suggestions
WORKDIR /suggestions
RUN gradle shadowJar --no-daemon

FROM openjdk:11.0.8-jre-slim
RUN mkdir /config/
COPY --from=build /suggestions/build/libs/Suggestions.jar /

ENTRYPOINT ["java", "-jar", "/Suggestions.jar"]
