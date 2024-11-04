FROM eclipse-temurin:21.0.5_11-jre-alpine

# Set the working directory
WORKDIR /opt/app

# Copy necessary files
COPY target/*.jar application.jar
#COPY .env .env
#COPY src/infra/wait-for-it.sh /opt/app/wait-for-it.sh
#RUN chmod +x /opt/app/wait-for-it.sh

# Add a non-root user and group more securely
RUN addgroup -S spring && adduser -S -G spring spring

# Switch to the non-root user
USER spring:spring

# Run the application
CMD ["java", "-jar", "application.jar"]
#CMD ["sh", "/opt/app/wait-for-it.sh", "redis", "6379", "--", "java", "-jar", "application.jar"]


#CMD ["/opt/app/wait-for-it.sh", "redis", "6379", "--", "sh", "-c", "java -jar application.jar"]
#CMD ["sh", "/opt/app/wait-for-it.sh", "url-shortener-redis", "6379", "--", "java", "-jar", "application.jar"]
#CMD ["java", "-jar", "application.jar"]
#CMD ["/opt/app/wait-for-it.sh", "redis", "6379", "--", "java", "-jar", "application.jar"]
#CMD ["/opt/app/wait-for-it.sh", "redis", "6379", "--", "sh", "-c", "java -jar application.jar"]
#CMD ["sh", "/opt/app/wait-for-it.sh", "redis", "6379", "--", "java", "-jar", "application.jar"]