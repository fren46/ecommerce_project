# Dockerfile
# create a application.yaml file in the same dir
FROM openjdk:11-jdk-slim
COPY build/libs/orderservice-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.properties .
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]