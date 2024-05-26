# Build state
FROM maven:3-openjdk-17 AS build
WORKDIR /usr/src/backend
# For better docker caching
COPY ./pom.xml ./pom.xml
RUN mvn dependency:resolve
# Build backend
COPY . .
RUN mvn package -DskipTests

# Run stage
FROM openjdk:17-slim
COPY --from=build /usr/src/backend/target/app.jar /app.jar
EXPOSE 8080
ENTRYPOINT java -jar -Dspring.profiles.active=$PROFILE /app.jar
