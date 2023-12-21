FROM openjdk:17

WORKDIR /app
COPY target/artifacts/demo_jar/demo.jar demo.jar

#CMD ["java -jar demo.jar"]
EXPOSE 7070/tcp

ENTRYPOINT ["/bin/bash", "-c", "java -jar /app/demo.jar"]