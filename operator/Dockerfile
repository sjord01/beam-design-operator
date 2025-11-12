# Runtime-only image for operator: assumes target/beam-operator-0.1.0-SNAPSHOT.jar is built locally
FROM eclipse-temurin:17-jre-jammy
ARG JAR=target/operator-0.0.1-SNAPSHOT.jar
COPY ${JAR} /app/beam-operator.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xms128m","-Xmx256m","-jar","/app/beam-operator.jar"]