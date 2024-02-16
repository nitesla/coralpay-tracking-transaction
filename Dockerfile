FROM openjdk:8-jdk-alpine
MAINTAINER CoralPay
EXPOSE 8080
COPY target/coralpay-tracking-transaction-1.0-SNAPSHOT.jar coralpay-tracking-transaction-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/coralpay-tracking-transaction-1.0-SNAPSHOT.jar"]
