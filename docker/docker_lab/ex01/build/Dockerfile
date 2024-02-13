FROM openjdk:11-jdk-slim

WORKDIR /app

COPY ./aws-v3-0.0.3.jar ./application.jar

ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=dev", "application.jar"]