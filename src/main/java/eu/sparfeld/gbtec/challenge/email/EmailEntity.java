package eu.sparfeld.gbtec.challenge.email;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "emails")
public class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "email", column = @Column(name = "from_email", nullable = false))
    private EmailAddress from;

    @ElementCollection
    @CollectionTable(name = "email_to_addresses", joinColumns = @JoinColumn(name = "email_id"))
    @Column(name = "to_email", nullable = false)
    private List<EmailAddress> to;

    @ElementCollection
    @CollectionTable(name = "email_cc_addresses", joinColumns = @JoinColumn(name = "email_id"))
    @Column(name = "cc_email")
    private List<EmailAddress> cc;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailState state;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmailAddress getFrom() {
        return from;
    }

    public void setFrom(EmailAddress from) {
        this.from = from;
    }

    public List<EmailAddress> getTo() {
        return to;
    }

    public void setTo(List<EmailAddress> to) {
        this.to = to;
    }

    public List<EmailAddress> getCc() {
        return cc;
    }

    public void setCc(List<EmailAddress> cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EmailState getState() {
        return state;
    }

    public void setState(EmailState state) {
        this.state = state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public enum EmailState {
        DRAFT,
        SENT,
        DELETED,
        SPAM
    }
}