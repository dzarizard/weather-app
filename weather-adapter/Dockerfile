FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar

WORKDIR /app
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
EXPOSE 8081