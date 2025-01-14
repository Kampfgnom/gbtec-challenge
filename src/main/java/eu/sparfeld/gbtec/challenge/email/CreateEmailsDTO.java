package eu.sparfeld.gbtec.challenge.email;

import java.util.List;

public record CreateEmailsDTO(
        List<CreateEmailDTO> emails
) {
}

