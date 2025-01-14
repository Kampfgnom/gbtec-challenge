# Coding Challenge

In this repository I solve a coding challenge.

## Technologies

Quite the standard setup:

* Java 21 - because LTS
* Spring Boot 3
* Spring WebMVC for the API
* Spring Data JPA for persistence
* Standard Spring scheduler for scheduling
* springdoc-openapi brings a Swagger UI
* docker compose for local development
* PostgreSQL as a database (14 because that's what Azure currently has)
* Flyway for schema migrations
* Some devtools, incl. spring-boot-docker-compose
* Tests in JUnit
  * Service unit tests
  * Controller unit tests with the new MockMvcTester
  * Integration tests with testcontainers and the new WebTestClient and awaitility

## Design & Architecture

Honestly, not a lot of architecture to see here.

* Layered architecture, as opposed to onion/hexagonal, because with WebMVC and JPA decoupling the layers is not worth the effort
* Separate DTOs (as Java Records) from Entities (as standard Java classes), because that's *definitely* worth the low effort
* Package-per-domain/use-case, because I find this better than package-per-layer
* Flat/no-packages until modules get too big, and not earlier (though the Java "one thing per file" convention makes me re-consider this opinion...)

## TODO / Shortcomings

* Tests are not as complete as I'd like
* CI/CD via GitHub Actions
* Observability: Logs, metrics, etc. via Spring standard technologies (micrometer etc.)
* Domain model is not good enough.
  * Email-State is not thought through well. Why can I transition from DRAFT to SENT for example.
  * What does the system *do*? Shouldn't I have email collections like a mail app normally has, which allow different operations
  * Delete isn't available at all, because I thought it would be redundant to state DELETED. Maybe rename state to TRASHED and allow deletion additionally.
* Security: User management and login (e.g. via OIDC provider integration) and access control (via Spring Security)
