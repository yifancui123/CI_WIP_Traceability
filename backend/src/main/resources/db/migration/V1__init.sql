
CREATE TABLE locations (
                           location_id BIGSERIAL PRIMARY KEY,
                           name        VARCHAR(100) NOT NULL UNIQUE,
                           zone        VARCHAR(50)
);

CREATE TABLE operators (
                           operator_id BIGSERIAL PRIMARY KEY,
                           full_name   VARCHAR(100) NOT NULL,
                           employee_no VARCHAR(50) UNIQUE,
                           created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE failure_codes (
                               failure_code_id BIGSERIAL PRIMARY KEY,
                               code            VARCHAR(30)  NOT NULL UNIQUE,   -- e.g. DAMAGED, EXPIRED
                               description     VARCHAR(200)
);

-- Current state
CREATE TABLE components (
                            component_id BIGSERIAL PRIMARY KEY,
                            part_number  VARCHAR(50)  NOT NULL,
                            name         VARCHAR(200) NOT NULL,
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
                               checked_by      BIGINT          REFERENCES operators(operator_id),
                               checked_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                               notes           TEXT
);

-- Indexes for the common "history of component X, newest first" queries
CREATE INDEX idx_movement_component ON movement_history(component_id, moved_at DESC);
CREATE INDEX idx_status_component   ON status_checks(component_id, checked_at DESC);
CREATE INDEX idx_components_part    ON components(part_number);
CREATE INDEX idx_status_failure     ON status_checks(failure_code_id);