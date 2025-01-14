package eu.sparfeld.gbtec.challenge.email;

import eu.sparfeld.gbtec.challenge.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        properties = {
                "gbtec.email.spam.email=test@gbtec.com",
                "gbtec.email.spam.cron=*/1 * * * * ?" // every second
        }
)
public class EmailSpamSchedulerTests {
    @MockitoSpyBean
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @Test
    void markEmailsAsSpam_shouldUpdateMatchingEmails() {
        String spamSender = "test@gbtec.com";

        emailService.createEmail(new CreateEmailDTO(
                new EmailAddressDTO(spamSender),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        ));

        EmailDTO secondEmail = emailService.createEmail(new CreateEmailDTO(
                new EmailAddressDTO(spamSender),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        ));

        emailService.updateEmail(secondEmail.id(), new UpdateEmailDTO(null, null, null, null, "SENT"));

        emailService.createEmail(new CreateEmailDTO(
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        ));

        verify(emailService, timeout(2500).atLeast(2)).markEmailsAsSpam(eq(spamSender));

        List<EmailDTO> updatedEmails = emailService.findAll().emails();

        assertThat(updatedEmails)
                .filteredOn(email -> email.from().email().equals(spamSender))
                .extracting(EmailDTO::state)
                .containsOnly(String.valueOf(EmailEntity.EmailState.SPAM));

        assertThat(updatedEmails)
                .filteredOn(email -> !email.from().email().equals(spamSender))
                .extracting(EmailDTO::state)
                .containsOnly(String.valueOf(EmailEntity.EmailState.DRAFT));
    }
}
