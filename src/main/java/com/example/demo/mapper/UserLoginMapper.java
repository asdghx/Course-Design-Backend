package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UserLogin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户登录 Mapper 接口
 * 继承 BaseMapper，提供基础 CRUD 方法
 */
@Repository
public interface UserLoginMapper extends BaseMapper<UserLogin> {

    /**
     * 根据账号和类型查询用户
     */
    UserLogin selectByAccountAndType(@Param("userAccount") String userAccount, @Param("userType") Integer userType);

    /**
     * 根据账号查询用户（不限制类型）
     */
    UserLogin selectByAccount(@Param("userAccount") String userAccount);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("userAccount") String userAccount, 
                       @Param("userPassword") String userPassword, 
                       @Param("saltValue") String saltValue);

}