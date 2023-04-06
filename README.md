# E-commerce RESTful API

This is an E-commerce RESTful API built using Java, Spring Boot, Spring Web, JPA, Spring MVC, OAuth2-Jwt, MySQL Docker container, Spring Security, and Apache Maven. The API includes security measures utilizing authentication and authorization in the form of user rights (Roles and Authorities).

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Features](#features)
- [Technologies](#technologies)
- [Contributing](#contributing)
- [License](#license)
- [Getting Started](#getting-started)
  - [Reference Documentation](#reference-documentation)
  - [Guides](#guides)

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

## Features

The E-commerce RESTful API includes the following features:

- User authentication and authorization with access levels in the form of Roles and Authorities
- User registration and login using OAuth2-Jwt
- User order history
- Product categories
- Product search
- Product filtering
- Shopping cart
- Checkout process
- Product Pages
- Admin dashboard (only accessible by authenticated users with admin privileges)
- Product management (only accessible by authenticated users with admin privileges)
- User profile management (only accessible by authenticated users with admin privileges)
- User role management (only accessible by authenticated users with admin privileges)
- User authority management (only accessible by authenticated users with admin privileges)
- Order management (only accessible by authenticated users with admin privileges)
- Category management (only accessible by authenticated users with admin privileges)

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
- Validation
- Lombok

## Contributing

If you'd like to contribute to the E-commerce RESTful API, please follow these steps:

1. Fork the repository
2. Create a new branch
3. Make your changes
4. Test your changes
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.






## Getting Started

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

