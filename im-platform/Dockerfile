FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . /workspace
RUN mvn -q -DskipTests package -pl im-platform -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/im-platform/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.config.location=/config/application-prod.yml"]
