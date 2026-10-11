CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(190) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE gigs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(60) NOT NULL,
    location VARCHAR(120),
    budget DECIMAL(12, 2) NOT NULL,
    created_by BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_gigs PRIMARY KEY (id),
    CONSTRAINT fk_gigs_created_by FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_gigs_status_created_at ON gigs (status, created_at);

CREATE TABLE gig_applications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    applicant_id BIGINT NOT NULL,
    gig_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_gig_applications PRIMARY KEY (id),
    CONSTRAINT uk_applicant_gig UNIQUE (applicant_id, gig_id),
    CONSTRAINT fk_gig_applications_applicant FOREIGN KEY (applicant_id)
        REFERENCES users (id),
    CONSTRAINT fk_gig_applications_gig FOREIGN KEY (gig_id) REFERENCES gigs (id)
);

