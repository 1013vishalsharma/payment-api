FROM openjdk:17-jdk-alpine
COPY target/payment-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]