package eu.sparfeld.gbtec.challenge.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("gbtec.email.spam")
public class EmailSpamSchedulerConfigurationProperties {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
