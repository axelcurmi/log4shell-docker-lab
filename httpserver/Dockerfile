FROM alpine
EXPOSE 3223
RUN apk add --no-cache openjdk8 python3
CMD /bin/sh -c "/usr/lib/jvm/java-8-openjdk/bin/javac /httpserver/source/*.java && \
                python3 -m http.server --directory /httpserver/source 3223"
