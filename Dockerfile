FROM maven:3.8-openjdk-18 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:18
WORKDIR /app
COPY --from=builder /app/target/social_network-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
