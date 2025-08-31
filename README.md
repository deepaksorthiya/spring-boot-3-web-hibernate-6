[![Java Maven Build Test](https://github.com/deepaksorthiya/spring-boot-3-web-hibernate-6/actions/workflows/maven-build.yml/badge.svg)](https://github.com/deepaksorthiya/spring-boot-3-web-hibernate-6/actions/workflows/maven-build.yml)
[![Docker Hub badge][dockerhub-badge]][dockerhub]

[dockerhub-badge]: https://img.shields.io/docker/pulls/deepaksorthiya/spring-boot-3-web-hibernate-6

[dockerhub]: https://hub.docker.com/repository/docker/deepaksorthiya/spring-boot-3-web-hibernate-6

---

Spring Boot Web Mvc JPA Hibernate Example

---

# Getting Started

## Requirements:

```
Git: 2.51.0
Spring Boot: 3.5.5
Maven: 3.9+
Java: 24
Database : POSTGRES-17/MYSQL-8/H2-2.2.224
Docker Desktop: Tested on 4.42.0
```

## Clone this repository:

```bash
git clone https://github.com/deepaksorthiya/spring-boot-3-web-hibernate-6.git
cd spring-boot-3-web-hibernate-6
```

## Build Project:

```bash
./mvnw clean package
```

## Run Project:

### Using H2 DB

```bash
./mvnw spring-boot:run -D"spring-boot.run.profiles=h2"
```

OR

```bash
java -D"spring.profiles.active=h2" -jar .\target\spring-boot-3-web-hibernate-6-0.0.1-SNAPSHOT.jar
```

### Using MYSQL DB (Docker Should Be Running)

```bash
docker compose --profile mysql up
```

```bash
./mvnw spring-boot:run -D"spring-boot.run.profiles=mysql"
```

OR

```bash
java -D"spring.profiles.active=mysql" -jar .\target\spring-boot-3-web-hibernate-6-0.0.1-SNAPSHOT.jar
```

### Using Postgres DB

```bash
docker compose --profile postgres up
```

### Start Both DB In Docker (Optional)

```bash
docker compose --profile postgres --profile mysql up
```

### Pass DB Credentials via command line(Optional)

```bash
./mvnw spring-boot:run  -D"spring-boot.run.profiles=postgres" -D'spring-boot.run.arguments="--spring.datasource.url=jdbc:postgresql://localhost:5432/testdb --spring.datasource.username=postgres --spring.datasource.password=postgres"'
```

OR

```bash
./mvnw spring-boot:run  -D"spring-boot.run.profiles=postgres" -D'spring-boot.run.jvmArguments="-Dspring.datasource.url=jdbc:postgresql://localhost:5432/testdb -Dspring.datasource.username=postgres -Dspring.datasource.password=postgres"'
```

OR

```bash
java -D"spring.profiles.active=postgres" -D"spring.datasource.url=jdbc:postgresql://localhost:5432/testdb" -D"spring.datasource.username=postgres" -D"spring.datasource.password=postgres" -jar .\target\spring-boot-3-web-hibernate-6-0.0.1-SNAPSHOT.jar
```

## h2 database console :

http://localhost:8080/h2-console

## Stop Docker Compose Service and Remove Volume

```bash
docker compose --profile postgres --profile mysql down -v
```

OR

```bash
docker compose --profile mysql down -v
```

OR

```bash
docker compose --profile postgres down -v
```

## Rest API Collection for Testing

API collection is available on postman. Install [POSTMAN](https://www.postman.com/downloads/) Use this link
[Postman API Collection](https://www.postman.com/deepaksorthiya/workspace/public-ws/collection/12463530-3a20e484-549d-4561-a11b-ef5d40b7fd3f?action=share&creator=12463530&active-environment=12463530-55c10ebe-548f-4c1b-a5ec-4d4ed996c033)

## I18 messages Testing

```bash
curl --location 'http://localhost:8080/api/v1/users/111' \
--header 'Accept-Language: hi'
```

```bash
curl --location 'http://localhost:8080/api/v1/users/111' \
--header 'Accept-Language: en-US'
```

## Reference Documentation

For further reference, please consider the following sections:

* [Best Way to Map ManyToMany](https://vladmihalcea.com/the-best-way-to-use-the-manytomany-annotation-with-jpa-and-hibernate/)
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/index.html#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/index.html#io.validation)

## Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

