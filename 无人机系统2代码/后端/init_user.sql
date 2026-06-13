CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    status INTEGER DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
);

INSERT INTO sys_user (username, password, real_name, email, phone, status, create_time, update_time)
SELECT 'admin', '85E371D0DB54CBA0F0432CDB3D03B418', '管理员', 'admin@example.com', '13800138000', 1, datetime('now'), datetime('now')
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');