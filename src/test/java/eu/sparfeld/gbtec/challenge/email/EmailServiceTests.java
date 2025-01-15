package eu.sparfeld.gbtec.challenge.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EmailServiceTests {

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private EmailService emailService;

    @Test
    void findById_shouldReturnEmailDTO_whenEmailExists() {
        EmailEntity mockEntity = new EmailEntity();
        mockEntity.setId(1L);
        mockEntity.setFrom(new EmailAddress("test@example.com"));
        mockEntity.setTo(List.of(new EmailAddress("recipient@example.com")));
        mockEntity.setCc(List.of(new EmailAddress("cc@example.com")));
        mockEntity.setSubject("Test Subject");
        mockEntity.setMessage("Test Message");
        mockEntity.setState(EmailEntity.EmailState.DRAFT);

        when(emailRepository.findById(1L)).thenReturn(Optional.of(mockEntity));

        EmailDTO result = emailService.findById(1L);

        assertThat(result)
                .isNotNull()
                .extracting(EmailDTO::subject, EmailDTO::state)
                .containsExactly("Test Subject", EmailStateDTO.DRAFT);
        verify(emailRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenEmailDoesNotExist() {
        when(emailRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailService.findById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email not found with ID: 1");
        verify(emailRepository, times(1)).findById(1L);
    }

    @Test
    void updateEmail_shouldUpdateFields_whenStateIsDraft() {
        EmailEntity mockEntity = new EmailEntity();
        mockEntity.setId(1L);
        mockEntity.setFrom(new EmailAddress("test@example.com"));
        mockEntity.setTo(List.of(new EmailAddress("recipient@example.com")));
        mockEntity.setCc(List.of(new EmailAddress("cc@example.com")));
        mockEntity.setSubject("Old Subject");
        mockEntity.setMessage("Old Message");
        mockEntity.setState(EmailEntity.EmailState.DRAFT);

        UpdateEmailDTO updateDTO = new UpdateEmailDTO(
                List.of(new EmailAddressDTO("newrecipient@example.com")),
                List.of(new EmailAddressDTO("newcc@example.com")),
                "New Subject",
                "New Message",
                EmailStateDTO.DRAFT
        );

        when(emailRepository.findById(1L)).thenReturn(Optional.of(mockEntity));
        when(emailRepository.save(any(EmailEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmailDTO result = emailService.updateEmail(1L, updateDTO);

        assertThat(result)
                .isNotNull()
                .extracting(EmailDTO::subject, EmailDTO::message)
                .containsExactly("New Subject", "New Message");
        assertThat(result.to())
                .extracting("email")
                .containsExactly("newrecipient@example.com");
        assertThat(result.cc())
                .extracting("email")
                .containsExactly("newcc@example.com");
    }

    @Test
    void updateEmail_shouldThrowException_whenStateIsNotDraft() {
        EmailEntity mockEntity = new EmailEntity();
        mockEntity.setId(1L);
        mockEntity.setState(EmailEntity.EmailState.SENT);

        UpdateEmailDTO updateDTO = new UpdateEmailDTO(null, null, "New Subject", "New Message", null);

        when(emailRepository.findById(1L)).thenReturn(Optional.of(mockEntity));

        assertThatThrownBy(() -> emailService.updateEmail(1L, updateDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only DRAFT emails can be updated.");
        verify(emailRepository, never()).save(any(EmailEntity.class));
    }


    @Test
    void markEmailsAsSpam_shouldUpdateStateToSpam_forMatchingEmails() {
        String spamRecipient = "carl@gbtec.com";

        EmailEntity email1 = new EmailEntity();
        email1.setId(1L);
        email1.setTo(List.of(new EmailAddress(spamRecipient)));
        email1.setState(EmailEntity.EmailState.DRAFT);

        EmailEntity email2 = new EmailEntity();
        email2.setId(2L);
        email2.setTo(List.of(new EmailAddress(spamRecipient)));
        email2.setState(EmailEntity.EmailState.SENT);

        List<EmailEntity> mockEmails = Arrays.asList(email1, email2);

        when(emailRepository.findByFrom_Email(spamRecipient)).thenReturn(mockEmails);

        emailService.markEmailsAsSpam(spamRecipient);

        assertThat(mockEmails)
                .extracting(EmailEntity::getState)
                .containsOnly(EmailEntity.EmailState.SPAM);

        assertThat(mockEmails)
                .extracting(EmailEntity::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void markEmailsAsSpam_shouldDoNothing_whenNoEmailsMatch() {
        String spamRecipient = "nonexistent@gbtec.com";

        when(emailRepository.findByFrom_Email(spamRecipient)).thenReturn(List.of());

        emailService.markEmailsAsSpam(spamRecipient);

        verify(emailRepository, times(1)).findByFrom_Email(spamRecipient);
    }
}