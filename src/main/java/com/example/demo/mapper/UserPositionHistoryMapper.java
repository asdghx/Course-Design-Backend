package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UserPositionHistory;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserPositionHistoryMapper extends BaseMapper<UserPositionHistory> {

    /**
     * 记录用户浏览岗位（只增加浏览次数）
     */
    @Insert("INSERT INTO user_position_history (user_account, position_id, browse_count, delivery_count, pass_count, update_time) VALUES (#{userAccount}, #{positionId}, 1, 0, 0, NOW()) ON DUPLICATE KEY UPDATE browse_count = browse_count + 1, update_time = NOW()")
    int recordBrowse(UserPositionHistory userPositionHistory);
    
    /**
     * 记录用户投递岗位（只增加投递次数）
     */
    @Insert("INSERT INTO user_position_history (user_account, position_id, browse_count, delivery_count, pass_count, update_time) VALUES (#{userAccount}, #{positionId}, 0, 1, 0, NOW()) ON DUPLICATE KEY UPDATE delivery_count = delivery_count + 1, update_time = NOW()")
    int recordDelivery(UserPositionHistory userPositionHistory);
    
    /**
     * 更新通过次数（只增加通过次数）
     */
    @Update("UPDATE user_position_history SET pass_count = pass_count + 1, update_time = NOW() WHERE user_account = #{userAccount} AND position_id = #{positionId}")
    void incrementPassCount(@Param("userAccount") String userAccount, @Param("positionId") Integer positionId);
    
    /**
     * 查询所有用户岗位浏览记录（用于协同过滤推荐算法）
     * @return 所有浏览记录列表
     */
    @Select("SELECT user_account, position_id, browse_count, delivery_count, pass_count, update_time FROM user_position_history")
    List<UserPositionHistory> getAllUserPositionHistories();
}