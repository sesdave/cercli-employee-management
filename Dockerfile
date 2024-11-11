# Use a base image with JDK (Java Development Kit)
FROM openjdk:17-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven or Gradle project files
COPY pom.xml .

# Download dependencies (this will cache dependencies if no changes in pom.xml)
RUN mvn dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use a smaller base image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port that the app will run on
EXPOSE 8080

# Set the command to run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
