package com.example.uav.service.impl;

import com.example.uav.domain.Uav;
import com.example.uav.repository.UavMapper;
import com.example.uav.service.IUavService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 无人机服务层实现
 */
@Service
public class UavServiceImpl implements IUavService {

    private static final Logger log = LoggerFactory.getLogger(UavServiceImpl.class);
    private static final Random RANDOM = new Random();

    @Autowired
    private UavMapper uavMapper;

    @Override
    public List<Uav> selectUavList(Uav uav) {
        return uavMapper.selectUavList(uav);
    }

    @Override
    public Uav selectUavById(Long id) {
        return uavMapper.selectUavById(id);
    }

    @Override
    public int insertUav(Uav uav) {
        Date now = new Date();
        uav.setCreateTime(now);
        uav.setUpdateTime(now);
        uav.setDeleted(0);
        return uavMapper.insertUav(uav);
    }

    @Override
    public int updateUav(Uav uav) {
        uav.setUpdateTime(new Date());
        return uavMapper.updateUav(uav);
    }

    @Override
    public int deleteUavById(Long id) {
        return uavMapper.deleteUavById(id, new Date());
    }

    @Override
    public Uav generateAiAttributes(String name, String type) {
        Uav uav = new Uav(name, type != null ? type : "多旋翼");
        String normalizedType = type != null ? type.toLowerCase() : "多旋翼";

        // 根据无人机类型和名称特征，AI 模拟生成合理的属性值
        if (normalizedType.contains("多旋翼") || normalizedType.contains("quad") || normalizedType.contains("multi")) {
            uav.setMaxFlightTime(25 + RANDOM.nextInt(26));   // 25-50 分钟
            uav.setMaxRange(5.0 + RANDOM.nextDouble() * 25); // 5-30 公里
            uav.setMaxAltitude(3000.0 + RANDOM.nextDouble() * 5000);
            uav.setPayloadCapacity(0.3 + RANDOM.nextDouble() * 2.0);
            uav.setBatteryCapacity(3000 + RANDOM.nextInt(4000));
            uav.setWeight(500.0 + RANDOM.nextDouble() * 1000);
        } else if (normalizedType.contains("固定翼") || normalizedType.contains("fixed") || normalizedType.contains("wing")) {
            uav.setMaxFlightTime(60 + RANDOM.nextInt(120));  // 60-180 分钟
            uav.setMaxRange(50.0 + RANDOM.nextDouble() * 200);
            uav.setMaxAltitude(3000.0 + RANDOM.nextDouble() * 7000);
            uav.setPayloadCapacity(1.0 + RANDOM.nextDouble() * 5.0);
            uav.setBatteryCapacity(5000 + RANDOM.nextInt(10000));
            uav.setWeight(2000.0 + RANDOM.nextDouble() * 8000);
        } else if (normalizedType.contains("直升机") || normalizedType.contains("heli") || normalizedType.contains("single")) {
            uav.setMaxFlightTime(40 + RANDOM.nextInt(50));   // 40-90 分钟
            uav.setMaxRange(20.0 + RANDOM.nextDouble() * 80);
            uav.setMaxAltitude(2000.0 + RANDOM.nextDouble() * 4000);
            uav.setPayloadCapacity(2.0 + RANDOM.nextDouble() * 10.0);
            uav.setBatteryCapacity(8000 + RANDOM.nextInt(12000));
            uav.setWeight(5000.0 + RANDOM.nextDouble() * 15000);
        } else {
            uav.setMaxFlightTime(30 + RANDOM.nextInt(40));
            uav.setMaxRange(10.0 + RANDOM.nextDouble() * 50);
            uav.setMaxAltitude(2000.0 + RANDOM.nextDouble() * 5000);
            uav.setPayloadCapacity(0.5 + RANDOM.nextDouble() * 3.0);
            uav.setBatteryCapacity(4000 + RANDOM.nextInt(5000));
            uav.setWeight(800.0 + RANDOM.nextDouble() * 2000);
        }

        // 保留两位小数
        uav.setMaxRange(Math.round(uav.getMaxRange() * 100.0) / 100.0);
        uav.setMaxAltitude(Math.round(uav.getMaxAltitude() * 100.0) / 100.0);
        uav.setPayloadCapacity(Math.round(uav.getPayloadCapacity() * 100.0) / 100.0);
        uav.setWeight(Math.round(uav.getWeight() * 100.0) / 100.0);

        // AI 生成描述
        uav.setDescription(generateDescription(uav));

        log.info("AI generated attributes for UAV: name={}, type={}", name, type);
        return uav;
    }

    /**
     * AI 根据属性生成描述文本
     */
    private String generateDescription(Uav uav) {
        return String.format(
                "该无人机为%s设计，型号 %s，最大飞行时间约%d分钟，最大飞行距离约%.1f公里，"
                        + "最高飞行高度约%.0f米，有效载荷约%.1f千克，电池容量%d mAh，重量约%.0f克。"
                        + "适用于%s场景。",
                uav.getType(),
                uav.getName(),
                uav.getMaxFlightTime(),
                uav.getMaxRange(),
                uav.getMaxAltitude(),
                uav.getPayloadCapacity(),
                uav.getBatteryCapacity(),
                uav.getWeight(),
                getScenario(uav.getType())
        );
    }

    private String getScenario(String type) {
        if (type == null) return "通用航拍和巡检";
        String t = type.toLowerCase();
        if (t.contains("多旋翼")) return "航拍、巡检、测绘和安防监控";
        if (t.contains("固定翼")) return "长距离巡航、大面积测绘和农业植保";
        if (t.contains("直升机")) return "工业级重载作业、物流运输和应急救援";
        return "航拍、巡检和测绘";
    }
}
