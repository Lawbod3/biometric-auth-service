-- schema.sql
CREATE TABLE IF NOT EXISTS finger_print (
                                            id UUID PRIMARY KEY,
                                            user_id VARCHAR(255) NOT NULL,
    finger VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS fingerprint_templates (
                                                     fingerprint_id UUID REFERENCES finger_print(id),
    template BYTEA NOT NULL
    );