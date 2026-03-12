# Stage 1: Build Stage
FROM bellsoft/liberica-runtime-container:jdk-25-stream-musl AS builder
WORKDIR /ws-builder
ADD . .
RUN chmod +x mvnw && ./mvnw -DskipTests -Dmaven.gitcommitid.skip=true -Pnative clean package

# Stage 2: Layer Tool Stage
# Perform the extraction in a separate builder container
FROM bellsoft/liberica-runtime-container:jdk-25-cds-slim-musl AS optimizer
WORKDIR /ws-optimizer
# This points to the built jar file in the target folder
# Adjust this to 'build/libs/*.jar' if you're using Gradle
# Copy the jar file to the working directory and rename it to application.jar
COPY --from=builder /ws-builder/target/*.jar application.jar
# Extract the jar file using an efficient layout
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted
# Stage 3: Final Stage
# This is the runtime container
FROM bellsoft/liberica-runtime-container:jre-25-stream-musl
WORKDIR /application
# Copy the extracted jar contents from the builder container into the working directory in the runtime container
# Every copy step creates a new docker layer
# This allows docker to only pull the changes it really needs
COPY --from=optimizer /ws-optimizer/extracted/dependencies/ ./
COPY --from=optimizer /ws-optimizer/extracted/spring-boot-loader/ ./
COPY --from=optimizer /ws-optimizer/extracted/snapshot-dependencies/ ./
COPY --from=optimizer /ws-optimizer/extracted/application/ ./
# Execute the AOT cache training run
RUN java -Dspring.aot.enabled=true -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf -Dspring.context.exit=onRefresh -jar application.jar
RUN java -Dspring.aot.enabled=true -XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=app.aot -jar application.jar && rm app.aotconf
# Start the application jar with AOT cache enabled - this is not the uber jar used by the builder
# This jar only contains application code and references to the extracted jar files
# This layout is efficient to start up and AOT cache friendly
ENTRYPOINT ["java", "-Dspring.aot.enabled=true", "-XX:AOTCache=app.aot", "-jar", "application.jar"]
# EXPOSE PORT
EXPOSE 8080