FROM eclipse-temurin:21.0.5_11-jre-alpine

# Set the working directory
WORKDIR /opt/app

# Copy necessary files
COPY target/*.jar application.jar

# Add a non-root user and group more securely
RUN addgroup -S spring && adduser -S -G spring spring

# Switch to the non-root user
USER spring:spring

# Run the application
CMD ["java", "-jar", "application.jar"]