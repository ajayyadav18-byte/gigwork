FROM maven:3.10.0-eclipse-temurin-26 AS build
WORKDIR /workspace
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline
COPY src src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:26-jre
WORKDIR /app
RUN groupadd --system --gid 10001 app \
    && useradd --system --uid 10001 --gid app --home-dir /app app
COPY --from=build --chown=10001:10001 /workspace/target/gigwork-*.jar /app/app.jar
USER 10001:10001
EXPOSE 10000
ENV PORT=10000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
