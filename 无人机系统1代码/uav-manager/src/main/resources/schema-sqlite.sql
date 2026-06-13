CREATE TABLE IF NOT EXISTS t_uav (
    id               INTEGER      PRIMARY KEY AUTOINCREMENT,
    name             VARCHAR(100) NOT NULL,
    type             VARCHAR(50),
    serial_number    VARCHAR(100) UNIQUE,
    max_flight_time  INT,
    max_range        REAL,
    max_altitude     REAL,
    payload_capacity REAL,
    battery_capacity INT,
    weight           REAL,
    status           VARCHAR(20)  DEFAULT 'active',
    description      TEXT,
    create_by        VARCHAR(64),
    create_time      DATETIME     NOT NULL,
    update_by        VARCHAR(64),
    update_time      DATETIME     NOT NULL,
    deleted          TINYINT      DEFAULT 0
);
