DELETE FROM drone;
DELETE FROM sqlite_sequence WHERE name='drone';
INSERT INTO drone (name, model, manufacturer, weight, max_altitude, max_speed,
    battery_capacity, flight_time, camera_resolution, status, create_time, update_time)
VALUES
('测试无人机1', 'T001', '测试厂商A', 1.5, 500, 100, 4000, 30, '4K', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('测试无人机2', 'T002', '测试厂商B', 2.0, 800, 120, 5000, 40, '4K', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('测试无人机3', 'T003', '测试厂商C', 1.2, 600, 90, 3500, 25, '1080P', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

DELETE FROM sys_user WHERE username = 'admin';
INSERT INTO sys_user (username, password, status, create_time, update_time)
VALUES ('admin', '367910497c6d4e73213f31aa1ee86a9a', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);