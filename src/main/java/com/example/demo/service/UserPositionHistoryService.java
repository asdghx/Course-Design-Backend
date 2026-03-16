package com.example.demo.service;

import com.example.demo.entity.UserPositionHistory;
import com.example.demo.mapper.UserPositionHistoryMapper;
import org.springframework.stereotype.Service;

/**
 * 用户岗位浏览历史服务类
 * 专注于记录用户的浏览、投递、通过等行为
 */
@Service
public class UserPositionHistoryService {

    private final UserPositionHistoryMapper userPositionHistoryMapper;

    public UserPositionHistoryService(UserPositionHistoryMapper userPositionHistoryMapper) {
        this.userPositionHistoryMapper = userPositionHistoryMapper;
    }

    /**
     * 记录岗位浏览行为
     */
    public String recordPositionBrowse(String userAccount, Integer positionId) {
        // 创建历史记录对象
        UserPositionHistory history = new UserPositionHistory();
        history.setUserAccount(userAccount);
        history.setPositionId(positionId);
        
        // 保存浏览记录
        int rows = userPositionHistoryMapper.recordBrowse(history);
        return rows > 0 ? "浏览记录保存成功" : "浏览记录保存失败";
    }

    /**
     * 记录岗位投递行为
     */
    public String recordPositionDelivery(String userAccount, Integer positionId) {
        // 创建历史记录对象
        UserPositionHistory history = new UserPositionHistory();
        history.setUserAccount(userAccount);
        history.setPositionId(positionId);
        
        // 保存投递记录
        int rows = userPositionHistoryMapper.recordDelivery(history);
        return rows > 0 ? "投递记录保存成功" : "投递记录保存失败";
    }

    /**
     * 记录岗位通过行为
     */
    public String recordPositionPass(String userAccount, Integer positionId) {
        // 增加通过次数
        userPositionHistoryMapper.incrementPassCount(userAccount, positionId);
        return "通过记录保存成功";
    }
}