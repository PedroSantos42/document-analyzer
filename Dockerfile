FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /api/files && chown -R spring:spring /api

WORKDIR /api

USER spring:spring

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} application.jar

ENTRYPOINT ["java","-jar","application.jar"]
