package eu.sparfeld.gbtec.challenge.email;

import java.util.List;

public class EmailMapper {
    private EmailMapper() {
        throw new IllegalStateException("Utility class");
    }

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
                toDTO(entity.getState()),
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
        return dtos.stream().map(EmailMapper::toEntity).toList();
    }

    private static List<EmailAddressDTO> toDTOList(List<EmailAddress> entities) {
        return entities.stream().map(EmailMapper::toDTO).toList();
    }

    private static EmailStateDTO toDTO(EmailEntity.EmailState state) {
        return switch (state) {
            case DRAFT -> EmailStateDTO.DRAFT;
            case SENT -> EmailStateDTO.SENT;
            case DELETED -> EmailStateDTO.DELETED;
            case SPAM -> EmailStateDTO.SPAM;
        };
    }

    static EmailEntity.EmailState toEntityState(EmailStateDTO state) {
        return switch (state) {
            case DRAFT -> EmailEntity.EmailState.DRAFT;
            case SENT -> EmailEntity.EmailState.SENT;
            case DELETED -> EmailEntity.EmailState.DELETED;
            case SPAM -> EmailEntity.EmailState.SPAM;
        };
    }
}
