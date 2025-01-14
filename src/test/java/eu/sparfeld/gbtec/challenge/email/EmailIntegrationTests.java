package eu.sparfeld.gbtec.challenge.email;

import eu.sparfeld.gbtec.challenge.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.awaitility.Awaitility.await;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "gbtec.email.spam.email=spammer@gbtec.com",
                "gbtec.email.spam.cron=*/1 * * * * ?" // every second
        }
)
@Import(TestcontainersConfiguration.class)
public class EmailIntegrationTests {
    WebTestClient client;
    @Autowired
    WebApplicationContext wac;

    @BeforeEach
    void setUp() {
        client = MockMvcWebTestClient.bindToApplicationContext(this.wac).build();
    }

    @Test
    void getEmail_shouldReturnEmailDTO() throws Exception {
        CreateEmailDTO mockRequest = new CreateEmailDTO(
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        );

        Long id = Objects.requireNonNull(client.post().uri("/emails")
                .bodyValue(mockRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmailDTO.class)
                .returnResult().getResponseBody()).id();


        client.get().uri("/emails/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.from.email").isEqualTo("test@example.com")
                .jsonPath("$.to[0].email").isEqualTo("recipient@example.com")
                .jsonPath("$.cc[0].email").isEqualTo("cc@example.com")
                .jsonPath("$.subject").isEqualTo("Subject")
                .jsonPath("$.message").isEqualTo("Message");
    }

    @Test
    void updateEmail_shouldUpdateEmail() throws Exception {
        CreateEmailDTO mockRequest = new CreateEmailDTO(
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        );

        UpdateEmailDTO updateRequest = new UpdateEmailDTO(
                List.of(new EmailAddressDTO("recipient2@example.com")),
                List.of(new EmailAddressDTO("cc2@example.com")),
                "Subject2",
                "Message2",
                "DRAFT"
        );

        Long id = Objects.requireNonNull(client.post().uri("/emails")
                .bodyValue(mockRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmailDTO.class)
                .returnResult().getResponseBody()).id();

        client.put().uri("/emails/" + id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.to[0].email").isEqualTo("recipient2@example.com")
                .jsonPath("$.cc[0].email").isEqualTo("cc2@example.com")
                .jsonPath("$.subject").isEqualTo("Subject2")
                .jsonPath("$.message").isEqualTo("Message2")
                .jsonPath("$.state").isEqualTo("DRAFT");

        client.get().uri("/emails/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.to[0].email").isEqualTo("recipient2@example.com")
                .jsonPath("$.cc[0].email").isEqualTo("cc2@example.com")
                .jsonPath("$.subject").isEqualTo("Subject2")
                .jsonPath("$.message").isEqualTo("Message2")
                .jsonPath("$.state").isEqualTo("DRAFT");
    }

    @Test
    void updateEmail_shouldUpdateStateToSent() {
        CreateEmailDTO mockRequest = new CreateEmailDTO(
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        );

        UpdateEmailDTO updateRequest = new UpdateEmailDTO(
                null,
                null,
                null,
                null,
                "SENT"
        );

        Long id = Objects.requireNonNull(client.post().uri("/emails")
                .bodyValue(mockRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmailDTO.class)
                .returnResult().getResponseBody()).id();

        client.put().uri("/emails/" + id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SENT");

        client.get().uri("/emails/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SENT");
    }

    @Test
    void updateEmail_shouldDisallowInvalidStateChanges() {
        CreateEmailDTO mockRequest = new CreateEmailDTO(
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        );

        UpdateEmailDTO updateRequest = new UpdateEmailDTO(
                null,
                null,
                null,
                null,
                "SENT"
        );

        UpdateEmailDTO updateRequest2 = new UpdateEmailDTO(
                null,
                null,
                null,
                null,
                "DRAFT"
        );
        Long id = Objects.requireNonNull(client.post().uri("/emails")
                .bodyValue(mockRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmailDTO.class)
                .returnResult().getResponseBody()).id();

        client.put().uri("/emails/" + id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SENT");

        client.put().uri("/emails/" + id)
                .bodyValue(updateRequest2)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateEmail_shouldDisallowPropertyChangesInSentState() {
        CreateEmailDTO mockRequest = new CreateEmailDTO(
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        );

        UpdateEmailDTO updateRequest = new UpdateEmailDTO(
                null,
                null,
                null,
                null,
                "SENT"
        );

        UpdateEmailDTO updateRequest2 = new UpdateEmailDTO(
                List.of(new EmailAddressDTO("recipient2@example.com")),
                null,
                null,
                null,
                null
        );
        Long id = Objects.requireNonNull(client.post().uri("/emails")
                .bodyValue(mockRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmailDTO.class)
                .returnResult().getResponseBody()).id();

        client.put().uri("/emails/" + id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SENT");

        client.put().uri("/emails/" + id)
                .bodyValue(updateRequest2)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void spammerEmailsShouldBeMarkedAsSpam() {
        CreateEmailDTO mockRequest = new CreateEmailDTO(
                new EmailAddressDTO("spammer@gbtec.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        );

        Long id = Objects.requireNonNull(client.post().uri("/emails")
                .bodyValue(mockRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmailDTO.class)
                .returnResult().getResponseBody()).id();

        await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            client.get().uri("/emails/" + id)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.state").isEqualTo("SPAM");
        });
    }
}