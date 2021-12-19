FROM maven:3.8.4-jdk-8 AS builder
RUN useradd builder
USER builder
WORKDIR /home/builder
COPY --chown=builder:builder . /home/builder
RUN mvn clean install -DskipTests

FROM openjdk:8u181-jdk-alpine
EXPOSE 8080
RUN addgroup -S runner && \
    adduser  -S runner -G runner
USER runner
WORKDIR /home/runner
COPY --from=builder /home/builder/target /home/runner
ENTRYPOINT [ "java", "-jar", "/home/runner/log4j-vuln-0.0.1-SNAPSHOT.jar" ]
