-- 检查 sys_user 表是否存在
SELECT name FROM sqlite_master WHERE type='table' AND name='sys_user';

-- 如果表不存在，创建表
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

-- 插入测试用户（密码是 MD5("baseplatform" + "admin123" + "md5salt")）
INSERT OR IGNORE INTO sys_user (username, password, real_name, email, phone, status, create_time, update_time)
VALUES ('admin', '85E371D0DB54CBA0F0432CDB3D03B418', '管理员', 'admin@example.com', '13800138000', 1, datetime('now'), datetime('now'));