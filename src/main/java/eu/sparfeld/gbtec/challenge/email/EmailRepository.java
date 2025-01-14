package eu.sparfeld.gbtec.challenge.email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
    List<EmailEntity> findByFrom_Email(String fromEmail);
}