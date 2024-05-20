FROM maven:3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/bankingtest-0.0.1.jar bankingtest.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","bankingtest.jar"]
