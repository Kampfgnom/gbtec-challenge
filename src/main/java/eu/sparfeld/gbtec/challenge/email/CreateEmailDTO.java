package eu.sparfeld.gbtec.challenge.email;

import java.util.List;

public record CreateEmailDTO(
        EmailAddressDTO from,
        List<EmailAddressDTO> to,
        List<EmailAddressDTO> cc,
        String subject,
        String message
) {
}


