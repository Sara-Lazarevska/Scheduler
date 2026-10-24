CREATE TABLE interview_slot (
                                id               BIGSERIAL PRIMARY KEY,
                                slot_date        DATE        NOT NULL,
                                start_time       TIME        NOT NULL,
                                duration_minutes INT         NOT NULL CHECK (duration_minutes > 0),
                                position         VARCHAR(100),
                                status           VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                                version          BIGINT      NOT NULL DEFAULT 0
);

CREATE TABLE booking (
                         id              BIGSERIAL PRIMARY KEY,
                         slot_id         BIGINT       NOT NULL UNIQUE REFERENCES interview_slot(id),
                         candidate_name  VARCHAR(100) NOT NULL,
                         candidate_email VARCHAR(255) NOT NULL,
                         notes           TEXT,
                         created_at      TIMESTAMP    NOT NULL DEFAULT now()
);