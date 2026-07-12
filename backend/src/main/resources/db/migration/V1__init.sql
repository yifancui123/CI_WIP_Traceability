-- Definition / catalog: Part -> Job -> Component
CREATE TABLE parts (
    part_id     BIGSERIAL    PRIMARY KEY,
    part_number VARCHAR(50)  NOT NULL UNIQUE,   -- e.g. P12345
    name        VARCHAR(200) NOT NULL,
    unit        VARCHAR(20)  NOT NULL           -- 'ml', 'box', 'each'
);

CREATE TABLE jobs (
    job_id      BIGSERIAL   PRIMARY KEY,
    job_number  VARCHAR(50) NOT NULL UNIQUE,           -- e.g. silicon123
    part_id     BIGINT      NOT NULL REFERENCES parts(part_id),
    expiry_date DATE        NOT NULL,
    is_deleted  BOOLEAN     NOT NULL DEFAULT false     -- soft delete; row stays so job_number is never reused
);

-- Lookup tables
CREATE TABLE locations (
    location_id BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    zone        VARCHAR(50)
);

CREATE TABLE operators (
    operator_id BIGSERIAL PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    employee_no VARCHAR(50) UNIQUE,
    role        VARCHAR(20)  NOT NULL DEFAULT 'OPERATOR',   -- OPERATOR or LEADER
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE failure_codes (
    failure_code_id BIGSERIAL PRIMARY KEY,
    code            VARCHAR(30)  NOT NULL UNIQUE,   -- e.g. DAMAGED, EXPIRED
    description     VARCHAR(200)
);

-- Current state: a physical split of a job
CREATE TABLE components (
    component_id BIGSERIAL    PRIMARY KEY,
    job_id       BIGINT       NOT NULL REFERENCES jobs(job_id),
    split_code   VARCHAR(60)  NOT NULL UNIQUE,       -- e.g. silicon123.001
    quantity     INT          NOT NULL CHECK (quantity >= 0),
    location_id  BIGINT       NOT NULL REFERENCES locations(location_id),
    status       VARCHAR(30)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Append-only history
CREATE TABLE movement_history (
    movement_id      BIGSERIAL PRIMARY KEY,
    component_id     BIGINT NOT NULL REFERENCES components(component_id),
    from_location_id BIGINT          REFERENCES locations(location_id),  -- null on first entry
    to_location_id   BIGINT NOT NULL REFERENCES locations(location_id),
    quantity_moved   INT    NOT NULL CHECK (quantity_moved > 0),
    moved_by         BIGINT          REFERENCES operators(operator_id),
    moved_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    notes            TEXT
);

CREATE TABLE status_checks (
    check_id        BIGSERIAL PRIMARY KEY,
    component_id    BIGINT NOT NULL REFERENCES components(component_id),
    status          VARCHAR(30) NOT NULL,
    failure_code_id BIGINT          REFERENCES failure_codes(failure_code_id), -- only on REJECTED
    quantity        INT             CHECK (quantity IS NULL OR quantity > 0),  -- units scrapped, set on a scrap/rejection
    checked_by      BIGINT          REFERENCES operators(operator_id),
    checked_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    notes           TEXT
);

-- Indexes
CREATE INDEX idx_jobs_part          ON jobs(part_id);
CREATE INDEX idx_components_job     ON components(job_id);
CREATE INDEX idx_movement_component ON movement_history(component_id, moved_at DESC);
CREATE INDEX idx_status_component   ON status_checks(component_id, checked_at DESC);
CREATE INDEX idx_status_failure     ON status_checks(failure_code_id);
