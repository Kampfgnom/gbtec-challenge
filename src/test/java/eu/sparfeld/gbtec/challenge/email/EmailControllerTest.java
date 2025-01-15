package eu.sparfeld.gbtec.challenge.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@WebMvcTest(EmailController.class)
class EmailControllerTest {
    @Autowired
    private MockMvcTester mockMvc;
    @MockitoBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getEmail_shouldReturnEmailDTO() {
        EmailDTO mockEmail = new EmailDTO(
                1L,
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message",
                EmailStateDTO.DRAFT,
                Instant.ofEpochMilli(123),
                Instant.ofEpochMilli(234)
        );

        given(emailService.findById(1L)).willReturn(mockEmail);

        assertThat(mockMvc.get().uri("/emails/1"))
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .convertTo(EmailDTO.class)
                .satisfies(body -> assertThat(body).isEqualTo(mockEmail));
    }

    @Test
    void getEmail_shouldReturn404WhenEmailDoesNotExist() {
        given(emailService.findById(1L)).willThrow(IllegalArgumentException.class);

        assertThat(mockMvc.get().uri("/emails/1"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void createEmail_shouldReturnEmailDTO() throws JsonProcessingException {
        CreateEmailDTO mockRequest = new CreateEmailDTO(
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message"
        );
        EmailDTO mockEmail = new EmailDTO(
                1L,
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message",
                EmailStateDTO.DRAFT,
                Instant.ofEpochMilli(123),
                Instant.ofEpochMilli(234)
        );

        given(emailService.createEmail(any())).willReturn(mockEmail);

        assertThat(mockMvc.post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRequest))
        )
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .convertTo(EmailDTO.class)
                .satisfies(body -> assertThat(body).isEqualTo(mockEmail));

        verify(emailService).createEmail(refEq(mockRequest));
    }

    @Test
    void createEmail_shouldReturn400() {

        assertThat(mockMvc.post().uri("/emails"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateEmail_shouldReturnEmailDTO() throws JsonProcessingException {
        UpdateEmailDTO mockRequest = new UpdateEmailDTO(
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message",
                EmailStateDTO.SENT
        );
        EmailDTO mockEmail = new EmailDTO(
                1L,
                new EmailAddressDTO("test@example.com"),
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message",
                EmailStateDTO.SENT,
                Instant.ofEpochMilli(123),
                Instant.ofEpochMilli(234)
        );

        given(emailService.updateEmail(eq(1L), any())).willReturn(mockEmail);

        assertThat(mockMvc.put()
                .uri("/emails/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRequest))
        )
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .convertTo(EmailDTO.class)
                .satisfies(body -> assertThat(body).isEqualTo(mockEmail));

        verify(emailService).updateEmail(eq(1L), refEq(mockRequest));
    }

    @Test
    void updateEmail_shouldReturn404WhenTheEmailDoesNotExist() throws JsonProcessingException {
        UpdateEmailDTO mockRequest = new UpdateEmailDTO(
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message",
                EmailStateDTO.SENT
        );

        given(emailService.updateEmail(eq(1L), any())).willThrow(new IllegalArgumentException("Email does not exist"));

        assertThat(mockMvc.put().uri("/emails/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRequest)))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void updateEmail_shouldReturn400WhenInputDataIsInvalid() throws JsonProcessingException {
        UpdateEmailDTO mockRequest = new UpdateEmailDTO(
                List.of(new EmailAddressDTO("recipient@example.com")),
                List.of(new EmailAddressDTO("cc@example.com")),
                "Subject",
                "Message",
                EmailStateDTO.SENT
        );

        given(emailService.updateEmail(eq(1L), any())).willThrow(new IllegalStateException("Invalid input data for email"));

        assertThat(mockMvc.put().uri("/emails/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRequest)))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void updateEmail_shouldReturn400WhenInputDataIsSyntacticallyInvalid() {
        assertThat(mockMvc.put().uri("/emails/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"state": "NOT_A_VALID_STATE" }
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
    }
}