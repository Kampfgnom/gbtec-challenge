package eu.sparfeld.gbtec.challenge.email;

import java.time.Instant;
import java.util.List;

public record EmailDTO(
        Long id,
        EmailAddressDTO from,
        List<EmailAddressDTO> to,
        List<EmailAddressDTO> cc,
        String subject,
        String message,
        String state,
        Instant createdAt,
        Instant updatedAt
) {
}
