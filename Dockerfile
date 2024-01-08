# Use a lightweight Java image
FROM openjdk:17-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file using a wildcard
COPY build/libs/fintrack-authentication-service-*.jar fintrack-authentication-service.jar

# Expose the port if your application uses a specific port
# EXPOSE 8080

# Specify the command to run on container startup
ENTRYPOINT ["java", "-jar", "fintrack-authentication-service.jar"]
