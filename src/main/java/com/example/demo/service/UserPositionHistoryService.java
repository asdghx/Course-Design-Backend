package com.example.demo.service;

import com.example.demo.entity.UserPositionHistory;
import com.example.demo.mapper.UserPositionHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class UserPositionHistoryService {

    @Autowired
    private UserPositionHistoryMapper userPositionHistoryMapper;

    @CacheEvict(value = "browseHistory", key = "#userAccount")
    public String recordPositionBrowse(String userAccount, Integer positionId) {
        if (userAccount == null || userAccount.trim().isEmpty()) {
            return "用户账号不能为空";
        }
        if (positionId == null) {
            return "岗位ID不能为空";
        }
        
        UserPositionHistory history = new UserPositionHistory();
        history.setUserAccount(userAccount);
        history.setPositionId(positionId);
        
        int rows = userPositionHistoryMapper.insertOrUpdateUserPositionHistory(history);
        return rows > 0 ? "浏览记录保存成功" : "浏览记录保存失败";
    }
}