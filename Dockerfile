# Start with a base image containing Java runtime
FROM azul/zulu-openjdk-alpine:11.0.7
# Make port available to the world outside this container
EXPOSE 8300

# The application's jar file
ARG JAR_FILE="build/libs/service.jar"

# Add the application's jar to the container
ADD ${JAR_FILE} service.jar

# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/service.jar"]
