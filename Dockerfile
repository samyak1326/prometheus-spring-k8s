# Start with a base image containing Java runtime
#FROM openjdk:8-jdk-alpine

FROM openjdk:21-jdk
# Add a volume pointing to /tmp
VOLUME /tmp

# Add the application's jar file
#ARG JAR_FILE=target/myapp-0.0.1-SNAPSHOT.jar

# Copy the jar file into the container
COPY ./target/demo-0.0.1-SNAPSHOT.jar .

EXPOSE 8080
# Run the jar file
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

ENTRYPOINT ["java" , "-jar", "demo-0.0.1-SNAPSHOT.jar"]