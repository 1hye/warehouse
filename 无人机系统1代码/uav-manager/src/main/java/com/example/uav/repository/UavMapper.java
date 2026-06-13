package com.example.uav.repository;

import com.example.uav.domain.Uav;
import org.apache.ibatis.annotations.*;
import java.util.Date;
import java.util.List;

/**
 * 无人机数据访问层（MyBatis Mapper）
 */
@Mapper
public interface UavMapper {

    /**
     * 查询无人机列表
     */
    @Select("<script>"
            + "SELECT * FROM t_uav WHERE deleted = 0"
            + "<if test='name != null and name != \"\"'>"
            + "<bind name='namePattern' value=\"'%' + name + '%'\" />"
            + " AND name LIKE #{namePattern}"
            + "</if>"
            + "<if test='status != null and status != \"\"'> AND status = #{status}</if>"
            + " ORDER BY create_time DESC"
            + "</script>")
    List<Uav> selectUavList(Uav uav);

    /**
     * 根据 ID 查询无人机
     */
    @Select("SELECT * FROM t_uav WHERE id = #{id} AND deleted = 0")
    Uav selectUavById(@Param("id") Long id);

    /**
     * 新增无人机
     */
    @Insert("<script>"
            + "INSERT INTO t_uav (name, type, serial_number, max_flight_time, max_range, "
            + "max_altitude, payload_capacity, battery_capacity, weight, status, description, "
            + "create_by, create_time, update_by, update_time, deleted) "
            + "VALUES (#{name}, #{type}, #{serialNumber}, #{maxFlightTime}, #{maxRange}, "
            + "#{maxAltitude}, #{payloadCapacity}, #{batteryCapacity}, #{weight}, #{status}, #{description}, "
            + "#{createBy}, #{createTime}, #{updateBy}, #{updateTime}, #{deleted})"
            + "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUav(Uav uav);

    /**
     * 修改无人机
     */
    @Update("<script>"
            + "UPDATE t_uav SET"
            + "<if test='name != null'> name = #{name},</if>"
            + "<if test='type != null'> type = #{type},</if>"
            + "<if test='serialNumber != null'> serial_number = #{serialNumber},</if>"
            + "<if test='maxFlightTime != null'> max_flight_time = #{maxFlightTime},</if>"
            + "<if test='maxRange != null'> max_range = #{maxRange},</if>"
            + "<if test='maxAltitude != null'> max_altitude = #{maxAltitude},</if>"
            + "<if test='payloadCapacity != null'> payload_capacity = #{payloadCapacity},</if>"
            + "<if test='batteryCapacity != null'> battery_capacity = #{batteryCapacity},</if>"
            + "<if test='weight != null'> weight = #{weight},</if>"
            + "<if test='status != null'> status = #{status},</if>"
            + "<if test='description != null'> description = #{description},</if>"
            + " update_by = #{updateBy}, update_time = #{updateTime}"
            + " WHERE id = #{id}"
            + "</script>")
    int updateUav(Uav uav);

    /**
     * 逻辑删除无人机
     */
    @Update("UPDATE t_uav SET deleted = 1, update_time = #{updateTime} WHERE id = #{id}")
    int deleteUavById(@Param("id") Long id, @Param("updateTime") Date updateTime);
}
