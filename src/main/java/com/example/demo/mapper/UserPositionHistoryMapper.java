package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UserPositionHistory;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPositionHistoryMapper extends BaseMapper<UserPositionHistory> {

    @Insert("INSERT INTO user_position_history (user_account, position_id, duration_seconds) VALUES (#{userAccount}, #{positionId}, #{durationSeconds})")
    int insertUserPositionHistory(UserPositionHistory userPositionHistory);
    

    
    @Select("SELECT * FROM user_position_history")
    List<UserPositionHistory> getAllUserPositionHistories();
}