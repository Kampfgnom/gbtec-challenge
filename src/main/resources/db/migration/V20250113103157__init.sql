-- Create the main emails table
CREATE TABLE emails
(
    id         BIGSERIAL PRIMARY KEY,
    from_email VARCHAR(255)                                       NOT NULL,
    subject    VARCHAR(255)                                       NOT NULL,
    message    TEXT,
    state      VARCHAR(20)                                        NOT NULL CHECK (state IN ('DRAFT', 'SENT', 'DELETED', 'SPAM')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Create the table for "to" email addresses
CREATE TABLE email_to_addresses
(
    email_id BIGINT       NOT NULL,
    email    VARCHAR(255) NOT NULL,
    FOREIGN KEY (email_id) REFERENCES emails (id) ON DELETE CASCADE
);

-- Create the table for "cc" email addresses
CREATE TABLE email_cc_addresses
(
    email_id BIGINT NOT NULL,
    email    VARCHAR(255),
    FOREIGN KEY (email_id) REFERENCES emails (id) ON DELETE CASCADE
);
