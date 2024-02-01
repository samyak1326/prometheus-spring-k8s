# Start with a base image containing Java runtime and Tomcat
FROM tomcat:latest

# Add a volume pointing to /tmp (optional, if you need to persist data in /tmp)
#VOLUME /tmp

# Remove default Tomcat web applications (optional, to start with a clean Tomcat)
#RUN #rm -rf /usr/local/tomcat/webapps/*

# Copy the .war file into the container into the webapps directory
COPY ./target/demo-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/

# Expose port 8080 for HTTP traffic
EXPOSE 8080


# The base image's CMD instruction starts Tomcat, so no need to provide a custom ENTRYPOINT or CMD
CMD ["catalina.sh", "run"]

# docker run -p 9091:9090 -v /E/Server_APdI/demo/src/main/resources/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus