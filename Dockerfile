# Dockerfile untuk Spring Boot WMS Application
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy file WAR hasil build Maven ke container
COPY target/*.war app.war

# Expose port aplikasi (8080)
EXPOSE 8080

# Jalankan aplikasi Spring Boot WAR
ENTRYPOINT ["java", "-jar", "app.war"]
