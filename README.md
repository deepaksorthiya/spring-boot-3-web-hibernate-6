# Getting Started

### Requirements:

```
Spring Boot: 3.3.5
Maven: 3.9+
Java: 17
Database : MYSQL/H2
Docker : Tested on 4.34.3 (170107)
```

Clone this repository:

```bash
git clone https://github.com/deepaksorthiya/spring-boot-3-web-hibernate-6.git
cd spring-boot-3-web-hibernate-6
```

### Build Project:

```bash
./mvnw clean package
```

# Run Project:

### Using H2 DB

```bash
./mvnw spring-boot:run -D"spring.profiles.active=h2"
```

### Using MYSQL DB (Docker Should Be Running)

```bash
docker compose up
```

```bash
./mvnw spring-boot:run -D"spring.profiles.active=mysql"
```

### h2 database console :

http://localhost:8080/h2-console

### Reference Documentation
For further reference, please consider the following sections:

* [Best Way to Map ManyToMany](https://vladmihalcea.com/the-best-way-to-use-the-manytomany-annotation-with-jpa-and-hibernate/)
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.5/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.1.5/reference/htmlsingle/index.html#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.1.5/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/docs/3.1.5/reference/htmlsingle/index.html#io.validation)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

