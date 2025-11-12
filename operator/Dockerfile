FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre-alpine
ARG JAR=target/beam-operator-0.1.0-SNAPSHOT.jar
COPY --from=build /src/${JAR} /app/beam-operator.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xms128m","-Xmx256m","-jar","/app/beam-operator.jar"]