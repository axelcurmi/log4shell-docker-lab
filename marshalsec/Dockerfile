FROM maven:3.8.4-jdk-8 AS builder
RUN useradd builder
USER builder
WORKDIR /home/builder
RUN git clone https://github.com/mbechler/marshalsec.git
RUN mvn -f marshalsec clean package -DskipTests

FROM openjdk:8u181-jdk-alpine
EXPOSE 1389
RUN addgroup -S runner && \
    adduser  -S runner -G runner
USER runner
WORKDIR /home/runner
COPY --from=builder /home/builder/marshalsec/target/marshalsec*all.jar /home/runner/marshalsec.jar
ENTRYPOINT [ "java", "-cp", "/home/runner/marshalsec.jar", "marshalsec.jndi.LDAPRefServer" ]
