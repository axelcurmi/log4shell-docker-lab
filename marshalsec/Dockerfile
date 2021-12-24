FROM maven:3.8.4-jdk-8 AS builder
WORKDIR /root
RUN git clone https://github.com/mbechler/marshalsec.git
RUN mvn -f marshalsec clean package -DskipTests

FROM openjdk:8u181-jdk-alpine
EXPOSE 1389
WORKDIR /root
COPY --from=builder /root/marshalsec/target/marshalsec*all.jar marshalsec.jar
ENTRYPOINT [ "java", "-cp", "marshalsec.jar", "marshalsec.jndi.LDAPRefServer" ]
