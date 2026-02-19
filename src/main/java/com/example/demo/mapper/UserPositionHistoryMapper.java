package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UserPositionHistory;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserPositionHistoryMapper extends BaseMapper<UserPositionHistory> {

    @Insert("INSERT INTO user_position_history (user_account, position_id, browse_count, last_browse_time) VALUES (#{userAccount}, #{positionId}, #{browseCount}, #{lastBrowseTime}) ON DUPLICATE KEY UPDATE browse_count = browse_count + 1, last_browse_time = NOW()")
    int insertOrUpdateUserPositionHistory(UserPositionHistory userPositionHistory);
    
    @Select("SELECT user_account, position_id, browse_count, last_browse_time FROM user_position_history")
    List<UserPositionHistory> getAllUserPositionHistories();
}