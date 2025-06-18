# Catalog Service

## Overview
The Catalog Service is a Spring Boot application designed to manage product catalogs in a microservices architecture. It provides RESTful APIs for creating, reading, updating, and deleting product information.The application is built with Java,Maven, and PostgreSQL as the database.

## Features
- **Product Management**: Create, read, update, and delete product information.
- **Database Integration**: Uses PostgreSQL for persistent storage.
- **Docker Support**: Can be containerized using Docker for easy deployment.
- **API Documentation**: Swagger UI for API documentation and testing.
- **Configuration Profiles**: Supports multiple environments (development, production, testing) through configuration profiles.
- **Testing**: Includes unit  tests to ensure code quality and reliability.

---

## Pre-requisites 
Before running the application, ensure you have the following installed:
1. **Java Development Kit (JDK)**: Version 21 or higher.
2. **Maven**: Version 3.6+ or higher.
3. **Docker**: Optional, for containerization.
4. **PostgreSQL**: Optional (if not using Docker Compose).

---

## How to Run the Project 
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Idodoo/catalog-service.git
    cd catalog-service
    ```
2. **Build the Project**:
   ```bash
   mvn clean install
   ```

3. **Run Locally (Without Docker)**:
     Ensure PostgreSQL is running locally and update the application-dev.properties file with your database credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/catalogdb
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```
    Then run the application:
    ```bash
    mvn spring-boot:run
    ```

4. **Run withDocker**:
   If you have Docker installed, you can run the application using Docker Compose:
   ```bash
   docker-compose up --build
   ```
   Run in detached mode:
   ```bash
    docker-compose up -d --build
    ```
   Stop and remove containers:
   ```bash   
     docker-compose down
     ```
   View logs:
   ```bash
     docker-compose logs -f
     ```
5. **Access the Application**:
    Open your web browser and navigate to:
    ```
    http://localhost:8085.
    ```
6. **API Documentation**:
   Access the Swagger UI for API documentation:
   ```
   http://localhost:8085/swagger-ui/index.html
   ``` 
---
## Configuration Details

### Environment Variables
The application uses the following environmentvariables:
- `SPRING_PROFILES_ACTIVE`: Specifies the active profile (e.g., `dev`, `prod`, `staging`).
- `DB_URL`: The URL for the PostgreSQL database.
- `DB_USERNAME`: The username for the PostgreSQL database.
- `DB_PASSWORD`: The password for the PostgreSQL database.

.dockerignore
Ensure unnecessary files are not included in the Docker image:
```
# Ignore Maven target directory
target/

# Ignore IDE files
.idea/

# Ignore logs
*.logs/
```
---
## Dependencies
- **Spring Boot Starter Web**: For building RESTful APIs.
- **Spring Boot Starter Data JPA**: For database interaction.
- **PostgreSQL Driver**: For connecting to PostgreSQL.
- **JUnit**: For unit testing.
---
## Testing
The project includes unit tests to ensure the functionality of the application. To run the tests, use the following command:

``` bash
     mvn test
```
---
## Contributors

- **Ivan Dodoo**: Project owner and maintainer.
   - Email: [dodoo.ivan17@gmail.com](mailto:dodoo.ivan17@gmail.com)
   - GitHub: [Idodoo](https://github.com/Idodoo)

## License
This project is licensed under the MIT License. See the [LICENSE](fff) file for details.