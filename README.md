# sentinelAuth 🛡️

A secure, high-performance, and lightweight backend authentication service built from scratch using Java and Spring Boot. This project focuses on stateless session management, secure token handling, and robust endpoint protection without relying on heavy third-party providers.

## 🚀 Key Features & Architectural Mechanics

*   **Stateless Token Management:** Implemented secure JSON Web Token (JWT) generation, cryptographic verification, and rotational token strategies to maintain secure, stateless user sessions.
*   **Deep Security Layering:** Configured a custom `SecurityFilterChain` to intercept incoming HTTP requests, parse tokens, and handle exceptions seamlessly before requests ever hit the controllers.
*   **Secure Authentication Engine:** Leveraged `DaoAuthenticationProvider` along with a custom `UserDetailsService` implementation for decoupled, clean user verification logic.
*   **Industry-Standard Hashing:** Ensured absolute data safety at rest by implementing `BCryptPasswordEncoder` for cryptographic password salting and hashing.

---

## 🛠️ Tech Stack & Dependencies

*   **Language:** Java
*   **Framework:** Spring Boot (Spring Web, Spring Security)
*   **Data Management:** Spring Data JPA / Hibernate
*   **Security & Identity:** `io.jsonwebtoken` (jjwt) for stateless tokens
*   **Database:** *H2*

---

## 🏗️ System Flow (How it Works)

1. **User Registration:** User details are sent -> Password is salted and hashed via BCrypt -> Saved to the database.
2. **User Login:** Credentials verified via `DaoAuthenticationProvider` -> Stateless JWT access token and refresh token are generated -> Sent back in the response header/body.
3. **Route Guarding:** Incoming requests pass through a custom JWT filter within the `SecurityFilterChain`. Valid tokens grant access; missing or expired tokens are immediately short-circuited with a `401 Unauthorized` status.

---

## 🚥 Core API Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Registers a new user account | ❌ No |
| **POST** | `/api/auth/login` | Authenticates credentials & returns JWT | ❌ No |
| **POST** | `/api/auth/refresh` | Rotates expired access tokens using a refresh token | ❌ No |
| **GET** | `/api/check` | A sample endpoint | 🔑 Yes |

---

## 💻 How to Run Locally

### Prerequisites
* JDK 17 or higher
* Maven
* H2 running locally

### Steps
1. Clone the repository:
```bash
   git clone https://github.com/Sukesh-Raj/sentinelAuth.git
   cd sentinelAuth
```
2. Configure your environment variables in src/main/resources/application.properties:

   spring.datasource.url=jdbc:[your-db-url-here]\
   spring.datasource.username=[your-username]\
   spring.datasource.password=[your-password]

3. Build and run the application:
```bash
   mvn clean install
   mvn spring-boot:run
```
