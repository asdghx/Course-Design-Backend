package com.example.demo.service;

import com.example.demo.entity.UserPositionHistory;
import com.example.demo.mapper.UserPositionHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户岗位浏览历史服务类
 * 记录和管理用户浏览岗位的历史信息
 */
@Service
public class UserPositionHistoryService {

    @Autowired
    private UserPositionHistoryMapper userPositionHistoryMapper;

    /**
     * 记录岗位浏览历史
     */
    @CacheEvict(value = "browseHistory", key = "#userAccount")
    public String recordPositionBrowse(String userAccount, Integer positionId) {
        // 参数验证
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return "用户账号不能为空";
        }
        if (positionId == null) {
            return "岗位ID不能为空";
        }
        
        UserPositionHistory history = new UserPositionHistory();
        history.setUserAccount(userAccount);
        history.setPositionId(positionId);
        history.setDurationSeconds(0); // 初始化浏览时长为0
        
        int rows = userPositionHistoryMapper.insertUserPositionHistory(history);
        return rows > 0 ? "浏览记录保存成功" : "浏览记录保存失败";
    }
    

}