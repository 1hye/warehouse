CREATE TABLE IF NOT EXISTS drone (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    model VARCHAR(50) NOT NULL,
    manufacturer VARCHAR(100),
    weight DOUBLE NOT NULL,
    max_altitude INTEGER DEFAULT 0,
    max_speed INTEGER DEFAULT 0,
    battery_capacity INTEGER NOT NULL,
    flight_time INTEGER NOT NULL,
    camera_resolution VARCHAR(50),
    status INTEGER DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_drone_name ON drone(name);
CREATE INDEX IF NOT EXISTS idx_drone_model ON drone(model);
CREATE INDEX IF NOT EXISTS idx_drone_status ON drone(status);

CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    status INTEGER DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_username ON sys_user(username);