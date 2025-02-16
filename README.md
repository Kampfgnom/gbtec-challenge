# Coding Challenge

In this repository I solve a coding challenge.

## Technologies

Quite the standard setup:

* Java 21 - because LTS
  * Sidenote: I've not done a lot of Java in a while (apart from reading library code and making small adjustments). Now I know that Kotlin is quite a lot more ergonomic...
* Spring Boot 3
* Spring WebMVC for the API
* Spring Data JPA for persistence
* Standard Spring scheduler for scheduling
* springdoc-openapi brings a Swagger UI
* docker compose for local development
* PostgreSQL as a database (16 because that's what Azure currently has)
* Flyway for schema migrations
* Some devtools, incl. spring-boot-docker-compose
* Tests in JUnit with assertj and mockito
  * Service unit tests
  * Controller unit tests with the new MockMvcTester
  * Integration tests with testcontainers and the new WebTestClient and awaitility

## Design & Architecture

Honestly, not a lot of architecture to see here.

* Layered architecture (Contoller, Service, Persistence), as opposed to onion/hexagonal, because with WebMVC and JPA decoupling the layers is not worth the effort
* Separate DTOs (as Java Records) from Entities (as standard Java classes), because that's *definitely* worth the low effort
* Package-per-domain/use-case, because I find this better than package-per-layer for reasons I can elaborate
* Flat/no-packages until modules get too big, and not earlier (though the Java "one thing per file" convention makes me re-consider this opinion...)

## TODO / Shortcomings

* [ ] Tests are not as complete as I'd like
* [x] Linting via sonarlint
* [x] [CI/CD via GitHub Actions](https://github.com/Kampfgnom/gbtec-challenge/actions/workflows/integration.yaml)
* [ ] Observability: Logs, metrics, etc. via Spring standard technologies (micrometer etc.)
* [ ] Domain model is not good enough (by me, I'm not blaming someone else on this).
  * Email-State is not thought through well. Why can I transition from DRAFT to SENT for example.
  * What does the system *do*? Shouldn't I have email collections like a mail app normally has, which allow different operations
  * Delete isn't available at all, because I thought it would be redundant to state DELETED. Maybe rename state to TRASHED and allow deletion additionally.
* [ ] Security: User management and login (e.g. via OIDC provider integration) and access control (via Spring Security)
