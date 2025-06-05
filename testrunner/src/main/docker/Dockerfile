# Use a lightweight OpenJDK image based on Alpine Linux
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy and extract the distribution zip
COPY ../../../../target/testrunner*-dist.zip app-dist.zip
RUN unzip app-dist.zip -d . \
    && rm app-dist.zip

# Expose the default port (change if your app uses a different one)
EXPOSE 8000

# Use exec form ENTRYPOINT with ARGS for parameter forwarding
ENTRYPOINT ["java", "-jar", "testrunner-1.0-SNAPSHOT.jar"]
CMD ["-p 8000"]
