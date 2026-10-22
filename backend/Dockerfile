# Render has no native Java runtime, so the backend ships as a container.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/trainer-*.jar app.jar
# Render free tier has 512 MB of RAM; keep the heap well inside it.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"
ENV SERVER_ADDRESS=0.0.0.0
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
