# Use a lightweight Java image
FROM openjdk:17-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file
COPY build/libs/authentication-service-0.0.1-SNAPSHOT.jar authentication-service.jar

# Expose the port if your application uses a specific port
# EXPOSE 8080

# Specify the command to run on container startup
ENTRYPOINT ["java", "-jar", "authentication-service.jar"]
