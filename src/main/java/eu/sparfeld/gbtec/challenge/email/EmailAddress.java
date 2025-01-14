package eu.sparfeld.gbtec.challenge.email;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class EmailAddress {
    private String email;

    protected EmailAddress() {
    }

    public EmailAddress(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}