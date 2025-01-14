package eu.sparfeld.gbtec.challenge.email;

import java.util.List;
import java.util.stream.Collectors;

public class EmailMapper {

    public static EmailEntity toEntity(CreateEmailDTO dto) {
        EmailEntity entity = new EmailEntity();
        entity.setFrom(toEntity(dto.from()));
        entity.setTo(toEntityList(dto.to()));
        entity.setCc(toEntityList(dto.cc()));
        entity.setSubject(dto.subject());
        entity.setMessage(dto.message());
        entity.setState(EmailEntity.EmailState.DRAFT);
        return entity;
    }

    public static EmailDTO toDTO(EmailEntity entity) {
        return new EmailDTO(
                entity.getId(),
                toDTO(entity.getFrom()),
                toDTOList(entity.getTo()),
                toDTOList(entity.getCc()),
                entity.getSubject(),
                entity.getMessage(),
                entity.getState().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private static EmailAddress toEntity(EmailAddressDTO dto) {
        return new EmailAddress(dto.email());
    }

    private static EmailAddressDTO toDTO(EmailAddress entity) {
        return new EmailAddressDTO(entity.getEmail());
    }

    public static List<EmailAddress> toEntityList(List<EmailAddressDTO> dtos) {
        return dtos.stream().map(EmailMapper::toEntity).collect(Collectors.toList());
    }

    private static List<EmailAddressDTO> toDTOList(List<EmailAddress> entities) {
        return entities.stream().map(EmailMapper::toDTO).collect(Collectors.toList());
    }
}
