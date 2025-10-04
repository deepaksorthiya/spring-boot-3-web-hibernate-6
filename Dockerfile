# Stage 1: Build Stage
FROM bellsoft/liberica-runtime-container:jdk-25-stream-musl AS builder

WORKDIR /home/app
COPY . /home/app/spring-boot-3-web-hibernate-6
WORKDIR /home/app/spring-boot-3-web-hibernate-6
RUN  chmod +x mvnw && ./mvnw -Dmaven.test.skip=true clean package

# Stage 2: Layer Tool Stage
FROM bellsoft/liberica-runtime-container:jdk-25-cds-slim-musl AS optimizer

WORKDIR /home/app
COPY --from=builder /home/app/spring-boot-3-web-hibernate-6/target/*.jar spring-boot-3-web-hibernate-6.jar
RUN java -Djarmode=tools -jar spring-boot-3-web-hibernate-6.jar extract --layers --launcher

# Stage 3: Final Stage
FROM bellsoft/liberica-runtime-container:jre-25-stream-musl

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
COPY --from=optimizer /home/app/spring-boot-3-web-hibernate-6/dependencies/ ./
COPY --from=optimizer /home/app/spring-boot-3-web-hibernate-6/spring-boot-loader/ ./
COPY --from=optimizer /home/app/spring-boot-3-web-hibernate-6/snapshot-dependencies/ ./
COPY --from=optimizer /home/app/spring-boot-3-web-hibernate-6/application/ ./