package eu.sparfeld.gbtec.challenge.email;


import java.util.List;

public record UpdateEmailDTO(
        List<EmailAddressDTO> to,
        List<EmailAddressDTO> cc,
        String subject,
        String message,
        String state
) {
}