FROM openjdk:17-alpine
EXPOSE 8081
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prd","app.jar"]