# MIP_Tema1 - Restaurant Management System

## Overview
This is a Java application designed for managing restaurant operations, including user authentication, order processing, and various interfaces for different user roles (client, manager, waiter).

## Technologies
-   **Java**
-   **Maven**
-   **JPA/Hibernate**

## Setup

1.  **Clone the repository**:
    ```bash
    git clone <repository_url>
    cd MIP_Tema1
    ```
    (Replace `<repository_url>` with your repository's URL.)

2.  **Configure Database**:
    Review and configure database settings in `src/main/resources/META-INF/persistence.xml` and potentially `config.json`.

3.  **Build the project**:
    ```bash
    mvn clean install
    ```

## Usage

Run the `Main.java` file (`src/main/java/org/example/Main.java`) from your IDE, or execute the generated JAR:

```bash
java -jar target/MIP_Tema1-<version>.jar
```
(Replace `<version>` with your project's version, e.g., `1.0-SNAPSHOT`.)
