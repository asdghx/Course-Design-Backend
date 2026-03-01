package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UserLogin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 用户登录Mapper接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD功能
 * 自定义扩展方法用于特殊业务场景
 */
@Repository
public interface UserLoginMapper extends BaseMapper<UserLogin> {

    /**
     * 根据用户账号和类型查询用户信息
     * @param userAccount 用户账号
     * @param userType 用户类型
     * @return 用户登录信息
     */
    UserLogin selectByAccountAndType(@Param("userAccount") String userAccount, @Param("userType") Integer userType);

}