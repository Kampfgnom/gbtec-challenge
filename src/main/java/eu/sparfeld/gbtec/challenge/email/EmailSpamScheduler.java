package eu.sparfeld.gbtec.challenge.email;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailSpamScheduler {
    private final EmailService emailService;
    private final EmailSpamSchedulerConfigurationProperties properties;

    public EmailSpamScheduler(EmailService emailService, EmailSpamSchedulerConfigurationProperties properties) {
        this.emailService = emailService;
        this.properties = properties;
    }

    @Scheduled(cron = "${gbtec.email.spam.cron}")
    public void markSpamEmails() {
        emailService.markEmailsAsSpam(properties.getEmail());
    }
}
