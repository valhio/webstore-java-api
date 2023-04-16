[![Java CI with Maven](https://github.com/valhio/webstore-java-api/actions/workflows/maven.yml/badge.svg)](https://github.com/valhio/webstore-java-api/actions/workflows/maven.yml)

# E-commerce RESTful API

This is an e-commerce RESTful API built using Java, Spring Boot, Spring Web, Spring Data JPA, Spring MVC, Spring
Security, OAuth2-Jwt, MySQL Docker container, Apache Maven and more. The API includes security measures utilizing Spring
Security and OAuth2-Jwt with user authentication and authorization features, in the form of user rights (user roles and
authorities), in order to restrict access to specific endpoints.

## Table of Contents

- [Key Features](#key-features)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies](#technologies)
- [Contributing](#contributing)
- [Features Documentation and Guides](#features-documentation-and-guides)
  - [Reference Documentation](#reference-documentation)
  - [Guides](#guides)
- [License](#license)

## Key Features

The E-commerce RESTful API includes the following features:

### Spring Security and JWT Configuration Features:

- SecurityConfiguration - Configures the security of a Spring Boot application using Spring Security. Provides a
  SecurityFilterChain bean to configure various security-related aspects of the application. Enables Spring Security
  module with @EnableWebSecurity annotation and sets up global method security with @EnableGlobalMethodSecurity
  annotation.
- JWTAccessDeniedHandler - Handles access denied errors that occur when a client tries to access a protected resource
  without proper authorization. Implements the AccessDeniedHandler interface from Spring Security.
- JWTAuthenticationEntryPoint - Handles authentication exceptions that occur when an unauthenticated user tries to
  access a protected resource. Extends the Http403ForbiddenEntryPoint class from Spring Security.
- JWTAuthorizationFilter - Validates the JWT token sent by the client in the Authorization header of the request.
  Extends the OncePerRequestFilter class. Responsible for setting the authentication in the security context if the JWT
  is valid.
- JWTTokenProvider - Generates and validates JWT tokens. Relies on the Auth0 library to create and verify tokens.

### Application Features:

- UserPrincipal class for representing users in the application's security context with necessary information for
  authentication and authorization purposes.
- JpaEnversConfiguration class for enabling caching and JPA auditing in a Spring application with Java-based
  configuration.
- SpringSecurityAuditorAware class for automatically setting auditing fields in entity classes with the username of the
  currently authenticated user.
- ExceptionHandling class for providing centralized handling of exceptions thrown by the application.
- UserController class for managing user-related operations such as user registration, login, retrieval, update, and
  deletion.
- ProductController class for handling products in a store with endpoints for creating, retrieving, and updating
  products.
- OrderController class for handling orders in a store with endpoints for creating, retrieving, and updating orders.
- LoginAttemptService class for keeping track of the number of failed login attempts for a user.

## Installation

To install the API, you'll need to follow these steps:

1. Clone the repository:

```bash
git clone ########
```

2. Install the dependencies:

```bash
mvn install
```

3. Set up the MySQL Docker container:

- Install Docker on your machine
- Run the following command in the terminal to start a MySQL container:
  ```bash
  docker run --name ecommerce-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=ecommerce -d mysql:latest
  ```

4. Set up the API:

- Configure the `application.properties` file with your database credentials
- Run the application using your IDE or using the command `mvn spring-boot:run`

## Usage

To use the API, you can send HTTP requests to the API endpoints using a tool like Postman. The base URL for the API is `http://localhost:8080/api/v1`.

## Technologies

The E-commerce RESTful API was built using the following technologies:

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- JDBC API
- Spring MVC
- OAuth2-Jwt
- MySQL Docker container
- Spring Security
- Apache Maven
- Spring Validation
- Lombok

## Contributing

If you'd like to contribute to the E-commerce RESTful API, please follow these steps:

1. Fork the repository
2. Create a new branch
3. Make your changes
4. Test your changes
5. Submit a pull request

## Features Documentation and Guides

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.5/maven-plugin/reference/html/#build-image)
* [Spring Web Services](https://docs.spring.io/spring-boot/docs/2.7.5/reference/htmlsingle/#io.webservices)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.7.5/reference/htmlsingle/#web)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.7.5/reference/htmlsingle/#using.devtools)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.7.5/reference/htmlsingle/#appendix.configuration-metadata.annotation-processor)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.7.5/reference/htmlsingle/#web.security)
* [JDBC API](https://docs.spring.io/spring-boot/docs/2.7.5/reference/htmlsingle/#data.sql)
* [Validation](https://docs.spring.io/spring-boot/docs/2.7.5/reference/htmlsingle/#io.validation)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.7.5/reference/htmlsingle/#data.sql.jpa-and-spring-data)

### Guides

The following guides illustrate how to use some features concretely:

* [Producing a SOAP web service](https://spring.io/guides/gs/producing-web-service/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Relational Data using JDBC with Spring](https://spring.io/guides/gs/relational-data-access/)
* [Managing Transactions](https://spring.io/guides/gs/managing-transactions/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

