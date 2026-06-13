const Database = require('better-sqlite3');
const path = require('path');

const dbPath = path.join(__dirname, 'example_db.sqlite');
const db = new Database(dbPath);

db.exec('DELETE FROM drone');

const insert = db.prepare(`
    INSERT INTO drone (name, model, manufacturer, weight, max_altitude, max_speed,
        battery_capacity, flight_time, camera_resolution, status, create_time, update_time)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'))
`);

const drones = [
    ['测试无人机1', 'T001', '测试厂商A', 1.5, 500, 100, 4000, 30, '4K', 1],
    ['测试无人机2', 'T002', '测试厂商B', 2.0, 800, 120, 5000, 40, '4K', 1],
    ['测试无人机3', 'T003', '测试厂商C', 1.2, 600, 90, 3500, 25, '1080P', 1],
    ['大疆精灵4', 'P4P', '大疆创新', 1.38, 6000, 72, 5870, 30, '4K', 1],
    ['大疆御2', 'Mavic2', '大疆创新', 0.91, 5000, 72, 3850, 31, '4K', 1],
    ['大疆经纬M300', 'M300', '大疆创新', 6.3, 7000, 80, 9000, 55, '4K', 1],
    ['Parrot Anafi', 'Anafi', 'Parrot', 0.32, 4000, 50, 2700, 25, '4K', 1],
    ['Autel EVO II', 'EVO2', 'Autel', 1.9, 6000, 72, 7100, 40, '8K', 1],
    ['飞鲨X1', 'X1', '飞鲨科技', 1.1, 4500, 95, 5000, 35, '4K', 1]
];

const insertMany = db.transaction((drones) => {
    for (const drone of drones) {
        insert.run(...drone);
    }
});

insertMany(drones);

const count = db.prepare('SELECT COUNT(*) as count FROM drone').get();
console.log(`无人机数据已更新，当前共 ${count.count} 条记录`);

db.close();