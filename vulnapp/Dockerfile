FROM maven:3.8.4-jdk-8 AS builder
WORKDIR /root
COPY --chown=builder:builder . /root
RUN mvn clean install -DskipTests

FROM openjdk:8u181-jdk-alpine
EXPOSE 8080
WORKDIR /root
ENV SUPERSECRETKEY ENV_SUPERDUPERSECRET
COPY --from=builder /root/target/log4shell-vulnapp*.jar log4shell-vulnapp.jar
ENTRYPOINT [ "java", "-jar", "log4shell-vulnapp.jar" ]
