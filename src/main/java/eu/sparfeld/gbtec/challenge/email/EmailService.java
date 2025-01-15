package eu.sparfeld.gbtec.challenge.email;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmailService {

    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Transactional(readOnly = true)
    public EmailsDTO findAll() {
        return new EmailsDTO(emailRepository.findAll().stream().map(EmailMapper::toDTO).toList());
    }

    @Transactional(readOnly = true)
    public EmailDTO findById(Long id) {
        EmailEntity emailEntity = emailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Email not found with ID: " + id));
        return EmailMapper.toDTO(emailEntity);
    }

    @Transactional
    public EmailsDTO createEmails(CreateEmailsDTO createEmailsDTO) {
        List<EmailEntity> entities = createEmailsDTO.emails().stream().map(email -> {
            EmailEntity emailEntity = EmailMapper.toEntity(email);
            emailEntity.setState(EmailEntity.EmailState.DRAFT);
            return emailEntity;
        }).toList();

        List<EmailEntity> savedEntities = emailRepository.saveAll(entities);

        return new EmailsDTO(savedEntities.stream().map(EmailMapper::toDTO).toList());
    }

    @Transactional
    public EmailDTO createEmail(CreateEmailDTO createEmailDTO) {
        EmailEntity emailEntity = EmailMapper.toEntity(createEmailDTO);
        emailEntity.setState(EmailEntity.EmailState.DRAFT);
        EmailEntity savedEntity = emailRepository.save(emailEntity);
        return EmailMapper.toDTO(savedEntity);
    }

    @Transactional
    public EmailDTO updateEmail(Long id, UpdateEmailDTO updateEmailDTO) {
        EmailEntity emailEntity = emailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Email not found with ID: " + id));

        if (updateEmailDTO.state() != null) {
            EmailEntity.EmailState newState = EmailMapper.toEntityState(updateEmailDTO.state());
            validateStateTransition(emailEntity.getState(), newState);
            emailEntity.setState(newState);
        }

        boolean changed = false;

        if (updateEmailDTO.to() != null) {
            emailEntity.setTo(EmailMapper.toEntityList(updateEmailDTO.to()));
            changed = true;
        }
        if (updateEmailDTO.cc() != null) {
            emailEntity.setCc(EmailMapper.toEntityList(updateEmailDTO.cc()));
            changed = true;
        }
        if (updateEmailDTO.subject() != null) {
            emailEntity.setSubject(updateEmailDTO.subject());
            changed = true;
        }
        if (updateEmailDTO.message() != null) {
            emailEntity.setMessage(updateEmailDTO.message());
            changed = true;
        }

        if (changed && emailEntity.getState() != EmailEntity.EmailState.DRAFT) {
            throw new IllegalStateException("Only DRAFT emails can be updated.");
        }

        return EmailMapper.toDTO(emailEntity);
    }

    @Transactional
    public void markEmailsAsSpam(String emailAddress) {
        List<EmailEntity> emails = emailRepository.findByFrom_Email(emailAddress);
        emails.forEach(email -> email.setState(EmailEntity.EmailState.SPAM));
    }

    private void validateStateTransition(EmailEntity.EmailState currentState, EmailEntity.EmailState newState) {
        switch (currentState) {
            case DRAFT:
                if (newState != EmailEntity.EmailState.DRAFT && newState != EmailEntity.EmailState.SENT && newState != EmailEntity.EmailState.DELETED && newState != EmailEntity.EmailState.SPAM) {
                    throw new IllegalStateException("Invalid state transition from DRAFT to " + newState);
                }
                break;
            case SENT:
                if (newState != EmailEntity.EmailState.SENT && newState != EmailEntity.EmailState.DELETED && newState != EmailEntity.EmailState.SPAM) {
                    throw new IllegalStateException("Invalid state transition from SENT to " + newState);
                }
                break;
            case SPAM:
                if (newState != EmailEntity.EmailState.SPAM && newState != EmailEntity.EmailState.DELETED) {
                    throw new IllegalStateException("Invalid state transition from SPAM to " + newState);
                }
                break;
            case DELETED:
                throw new IllegalStateException("No transitions allowed from DELETED state.");
            default:
                throw new IllegalArgumentException("Unknown state: " + currentState);
        }
    }
}